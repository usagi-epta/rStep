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


int init_serial( char* dev_name, speed_t speed ){
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
  pmode.c_cc [VMIN] = 1;
  pmode.c_cc [VTIME] = 0;

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
		printf("\tGot [0x%02X] (%c)\n",c,c);
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
