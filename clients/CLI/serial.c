/*
    Copyright (c) 2010, Reza Naima <reza@reza.net>
    All rights reserved.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

/* A chunk of code was replace with		     */
/* code from Brian Kasper <brian.P.Kasper@aero.org>  */
/* http://cygwin.com/ml/cygwin/2001-04/msg00549.html */

#include <sys/time.h>
#include <sys/select.h>
#include <sys/ioctl.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <fcntl.h>
#include <ctype.h>
#include <time.h>
#include <termios.h>
#include <string.h>
#include <inttypes.h>


typedef struct speed_map {
	speed_t		speed;
	char		*text;
} speed_map;

speed_t getSpeed(char* speed) {
	int index = 0;
	speed_map map[6] = {
		{B38400, "38400"},
		{B19200, "19200"},
		{B4800, "4800"},
		{B9600, "9600"},
		{B1200, "1200"},
		{0, 0}
	};
	while( map[index].text ) {
		if (!strcmp(map[index].text, speed))
			return map[index].speed;
		index++;
	}
	return 0; //not found
}

int posix_com_open(char *devicename, int rate, char parity, int databits,
                        int stopbits, int options) {
  int rtnval = 0;
  struct termios t;

  int local_databits = 0, local_stopbits = 0, local_parity = 0, local_rate = 0;
  char upper_parity = 0;

  /* Check for valid values */

  upper_parity = toupper((int)parity);

  if (((databits == 5) || (databits == 6) || (databits == 7) || (databits == 8)) &&
      ((stopbits == 2) || (stopbits == 1)) &&
      ((upper_parity == 'N') || (upper_parity == 'O') || (upper_parity == 'E')) &&
      ((rate == 50) || (rate == 75) || (rate == 110) || (rate == 134) || (rate == 150) ||
       (rate == 200) || (rate == 300) || (rate == 600) || (rate == 1200) || (rate == 1800) ||
       (rate == 2400) || (rate == 4800) || (rate == 9600) || (rate == 19200) || (rate == 38400) ||
       (rate == 57600) || (rate == 115200)))
  {

    /* Open the com port for read/write access */

    rtnval = open(devicename,O_RDWR);

    if (rtnval != -1)
    {
      /* Set the parity, data bits, and stop bits */

      switch(databits) {
        case 5 : local_databits = CS5; break;
        case 6 : local_databits = CS6; break;
        case 7 : local_databits = CS7; break;
        case 8 : local_databits = CS8; break;
      }

      if (stopbits == 2)
        local_stopbits = CSTOPB;
      else
        local_stopbits = 0;

      switch(upper_parity) {
        case 'E' : local_parity = PARENB; break;
        case 'O' : local_parity |= PARODD; break;
      }
    }

    t.c_iflag = IGNPAR;
    t.c_oflag = 0;
    t.c_cflag &= ~CSIZE; /* Zero out the CSIZE bits */
    t.c_cflag = CLOCAL | local_databits | local_stopbits | local_parity | options;
    t.c_lflag = IEXTEN;

    /* Set the data rate */

    switch(rate)
    {
      case 50     : local_rate = B50;     break;
      case 75     : local_rate = B75;     break;
      case 110    : local_rate = B110;    break;
      case 134    : local_rate = B134;    break;
      case 150    : local_rate = B150;    break;
      case 200    : local_rate = B200;    break;
      case 300    : local_rate = B300;    break;
      case 600    : local_rate = B600;    break;
      case 1200   : local_rate = B1200;   break;
      case 1800   : local_rate = B1800;   break;
      case 2400   : local_rate = B2400;   break;
      case 4800   : local_rate = B4800;   break;
      case 9600   : local_rate = B9600;   break;
      case 19200  : local_rate = B19200;  break;
      case 38400  : local_rate = B38400;  break;
#ifndef __OS_IRIX
      case 57600  : local_rate = B57600;  break;
      case 115200 : local_rate = B115200; break;
#endif
    }

    if ((cfsetispeed(&t,local_rate) != -1) && (cfsetospeed(&t,local_rate) != -1))
    {
      if (tcsetattr(rtnval,TCSANOW,&t) == -1)
        rtnval = -1;
    }
    else
      rtnval = -1;
  }
  else
    rtnval = -1;

  return(rtnval);
}

int posix_com_close(int port)
{
  int rtnval = 0;
  if (port != -1)
  {
    rtnval = (int) close(port);
  }

  return(rtnval);
}


int init_serial(char* dev_name, speed_t speed) {
  return posix_com_open(dev_name, 9600, 'N', 8, 1, 0);
}

int old_init_serial( char* dev_name, speed_t speed ){
  struct termios pmode;
  int serline;
  
  /* Open port and set serial attributes */
  if ((serline = open(dev_name, O_RDWR | O_NOCTTY | O_NONBLOCK)) < 0) {
    printf("Unable to open serial port %s\n",dev_name);
    exit(-1);
  }  

  tcgetattr(serline, &pmode);

  //cfmakeraw(&pmode);
  pmode.c_iflag &= ~(INPCK | IXOFF | IXON);
  pmode.c_cflag &= ~(HUPCL | CSTOPB | CRTSCTS);
  pmode.c_cflag |= (CLOCAL | CREAD);
  pmode.c_cc[VMIN] = 1;
  pmode.c_cc[VTIME] = 0;

  cfsetispeed(&pmode, speed);
  cfsetospeed(&pmode, speed);
  tcsetattr(serline, TCSANOW, &pmode);

  /* Clear O_NONBLOCK flag.  */
  int flags = fcntl(serline, F_GETFL, 0);
  if (flags == -1) { 
	  printf("Some error occored\n"); 
  }
  flags &= ~O_NONBLOCK;
  if (fcntl(serline, F_SETFL, flags) == -1) { 
	  printf("Some other error occured\n");
  }

  return serline;
}


int send_text(int fd, char* data, unsigned int length) {
	unsigned int ret = 1;
	while (length && ret > 0) {
		ret = write(fd, data, length);
		length = length - ret;
	}
	if (length) {
		printf("***** Unable to send all text\n");
	}
	return length;
}

int send_char(int fd, unsigned char data) {
	return send_text(fd, &data, 1);
}
	
int read_text(int fd, char* data, unsigned int length) {
	unsigned int ret = 1;
	unsigned int orig = length;
	
	while (length && (ret > 0)) {
		ret = read(fd, data, length);
		length = length - ret;
	}
	return orig-length;
}	

int read_textAll(int fd, char* data, unsigned int length) {
	unsigned int ret = 1;
	
	while (length) {
		ret = read(fd, data, length);
		length = length - ret;
	}
	return length;
}

	
unsigned char read_char(int fd) {
	unsigned char c;
	read_text(fd, &c, 1);
	return c;
}	

uint16_t read_line(int fd, uint8_t *buff) {
	uint16_t i=0;
	uint8_t c;

	while ( (c=read_char(fd)) != '\n' ) {
		if (c == 0x00) continue;
//		printf("\tGot [0x%02X] (%c)\n",c,c);
		buff[i++] = c;	
	}
	buff[i+1] = 0;
	return i;
}

void flushRead2(int fd) {
        char data[1024];
        int ret;

        bzero(data, 1024);
        ret = read(fd, data, 1024);
        printf("Flushed %s\n", data);
}


void flushRead(int fd) {
	int maxfd = fd+1;  /* maximum bit entry (fd) to test */
	fd_set readfs;
	struct timeval tv;

	tv.tv_sec = 0;
	tv.tv_usec = 500;
	FD_ZERO(&readfs);
	while (1) {
		FD_SET(fd, &readfs);  /* set testing for source 1 */
		select(maxfd, &readfs, NULL, NULL, &tv);
		if (FD_ISSET(fd, &readfs)) {
			flushRead2(fd);
		} else {
			return;
		}
	}
}
