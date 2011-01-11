/* Copyright (c) 2008, Reza Naima <reza@reza.net> All rights reserved.  
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

#include <ncurses.h>
#include "serial.h"
#include <sys/types.h>
#include <sys/stat.h>
#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <errno.h>
#include <sys/mman.h>
#include <inttypes.h>
#include <unistd.h>

uint8_t str[1024];
int serial_fd = -1;

void help(void) {
	printf("-p <serial/usb port> -g <gcode>\r\n");
}

void tx(uint8_t *buff) {
	uint8_t buff2[1024];
	printf("Sending: %s",buff);
	send_text( serial_fd, buff, strlen(buff));
	read_line(serial_fd, buff2);
	if (strncmp(buff2,"ok",2)) {
		printf("Did not receive ok, got %s instead\n",buff2);
	}
}


int main (int argc, char** argv) {
	int8_t i;
	char *port = NULL;
	char rate[] = "9600";
	uint8_t buff[1024];
	uint8_t dist=0;
	uint8_t blah=0;
	float mult=1;

	while ((i = getopt(argc, argv, "hp:g:")) != -1) {
		switch (i) {
			case 'p':
				port = optarg;
				break;
			case 'h':
				help();
				return 1;
			default:
				help();
				break;
		}
	}
	if (port == NULL ) {
		help();
		return 1;
	}

	serial_fd = init_serial(port, getSpeed(rate));
	if (serial_fd == -1) {
		perror("Unable to open serial device");
		return -1;
	}

	//wait for 'start' command
	printf("Waiting for mill to wakeup..");
	read_line(serial_fd, buff);
	if (strncmp(buff,"start",5)) {
		printf("\n..Did not receive start, got %s instead\n",buff);
		return -3;
	} else {
		printf("OK!\n");
	}
	tx("G21\n"); //mm
	tx("G91\n"); //incremental

	//input data
	initscr();
	refresh();
	while( (i=getch()) != 'q') {
		if (i >= '0' && i <= '9') {
			i -= '0';
			if (blah == 1) {
				dist = dist * 10 + i;
			} else if (blah == 0) {
				dist = i;
			}
		}
		if (i >= 'A' && i <= 'Z') {
			mult = 10;
			i += 'a'-'A';
		} else {
			mult = 1;
		}
		switch(i) {
			case 'j':
				i *= -1;
			case 'l':
				sprintf(buff,"G1 X%f\n", (mult*(float)i*0.001));
				tx(buff);	
				break;
			case 'k':
				i *= -1;
			case 'i':
				sprintf(buff,"G1 Y%f\n", (mult*(float)i*0.001));
				tx(buff);
				break;
			case 'g':
				i *= -1;
			case 't':
				sprintf(buff,"G1 Z%f\n", (mult*(float)i*0.001));
				tx(buff);
				break;
		}
	}

	return 0;
}
