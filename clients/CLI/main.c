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
#ifdef NCURSES
#include <ncurses/ncurses.h>
#endif
#include <termios.h>

uint8_t str[1024];
int serial_fd = -1;
uint16_t lineNum=0;

void help(void) {
	printf("-p <serial/usb port> -g <gcode>\r\n");
}


int tx(uint8_t *buff) {
	uint8_t buff2[256];
	printf("Transmitting :%s",buff);
	send_text(serial_fd, buff, strlen(buff));
	read_line(serial_fd, buff2);
	if (strncmp(buff2,"ok",2)) {
		printf("Did not receive ok, got %s instead\n",buff);
		return 0;
	}
	return 1;
}

uint8_t getChar(void) {
	uint8_t c;
	//scanf("%c",&c);
	c = getchar();	
//	c = getch(); //ncurses
	return c;
}


#ifdef NCURSES
#define myReturn printf("GOOZ\n"); tcsetattr( fileno( stdin ), TCSANOW, &oldSettings ); return
#else  
#define myReturn return
#endif

struct termios oldSettings, newSettings;

int main (int argc, char** argv) {
	int8_t i;
	char *port = NULL;
	char *gcode = NULL;
	char rate[] = "9600";
	uint8_t buff[1024];
	uint8_t buff2[1024];
	FILE *file_fd; 
	uint8_t ret;

	tcgetattr( fileno( stdin ), &oldSettings );
	newSettings = oldSettings;
	newSettings.c_lflag &= (~ICANON & ~ECHO);
	tcsetattr( fileno( stdin ), TCSANOW, &newSettings );   


#ifdef NCURSES
	//ncurses init
	initscr();
	cbreak();
#endif

	while ((i = getopt(argc, argv, "hp:g:")) != -1) {
		switch (i) {
			case 'p':
				port = optarg;
				break;
			case 'g':
				gcode = optarg;
				break;
			case 'h':
				help();
				myReturn 1;
			default:
				help();
				break;
		}
	}
	if (port == NULL || gcode == NULL) {
		help();
		myReturn 1;
	}

	serial_fd = init_serial(port, getSpeed(rate));
	if (serial_fd == -1) {
		perror("Unable to open serial device");
		myReturn -1;
	}

	//open file
	file_fd = fopen(gcode,	"r");
	if (!file_fd) {
		perror("Unable to open gcode file");
		myReturn -2;
	}
	//wait for 'start' command
	printf("Waiting for mill to wakeup..");
	read_line(serial_fd, buff2);
	if (strncmp(buff2,"START",5)) {
		printf("\n..Did not receive start, got %s instead\n",buff2);
		myReturn -3;
	} else {
		printf("OK!\n");
	}



	// position head to start position
	printf("Set Home Position\n");
	tx("G91\n"); //incremental
	tx("G21\n"); //mm
	printf("Change Bit [j] DOWN [k] up [l] right [h] left [!] resume\n");
	while ( (ret = getChar()) != '!') {
		switch(ret) {
			case 'j':
				tx("G0 Y1 \n");
				break;
			case 'k':
				tx("G0 Y-1 \n");
				break;
			case 'h':
				tx("G0 X-1\n");
				break;
			case 'l':
				tx("G0 X1\n");
				break;
			default:
				printf("Unkown Command %c\n",ret);
				break;
		}
	} 
	tx("G90\n"); //absolute
	tx("G92\n"); //set as home

			
	// read a line and send it the serial port
	while(fgets(buff,sizeof(buff),file_fd) != NULL) {
		lineNum++;
		if (buff[0] == 'T' ) {
			printf("Tooling Change: %s",buff);
			tx("G00 Z20\n");
			tx("G00 X0 Y0\n");
			tx("G91\n"); //incremental
			printf("Change Bit [j] DOWN [k] up [*] AUTO [!] resume\n");
			while ( (ret = getChar()) != '!') {
				switch(ret) {
					case 'j':
						printf("up\n");
						tx("G0 Z1 \n");
						break;
					case 'k':
						printf("down\n");
						tx("G0 Z-1 \n");
						break;
					case 'J':
						printf("up\n");
						tx("G0 Z5 \n");
						break;
					case 'K':
						printf("down\n");
						tx("G0 Z-5 \n");
						break;
					case '*':
						printf("Auto - Make sure aligator clips are connected");
						tx("M250\n");
						break;
					default:
						printf("Unkown Command %c\n",ret);
						break;
				}
			} 
			tx("G90\n"); //absolute
			tx("G92\n"); //set as home
			tx("G0 Z2\n"); //asusming rest of gcode is absolute
		}
		if (buff[0] != 'M' && buff[0] != 'G') {
			printf("Skipping Comment (%c): %s ",buff[0],buff);
			continue;
		}
		printf(">%d< %s\r\n",lineNum,buff);
		send_text(serial_fd, buff, strlen(buff));
		do {
			read_line(serial_fd, buff2);
			printf("<%d> %s\r\n", lineNum, buff2);
		} while (strncmp(buff2,"OK",2));
		
	}
	printf("Done!\n");
	close(serial_fd);
	fclose(file_fd);
#ifdef NCURSES
	endwin();
	tcsetattr( fileno( stdin ), TCSANOW, &oldSettings );   
#endif
	return 0;
}
