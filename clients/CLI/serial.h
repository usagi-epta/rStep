/* serial.c */
#include <sys/time.h>
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

speed_t getSpeed(char *speed);
int init_serial(char *dev_name, speed_t speed);
int main(int argc, char **argv);
int send_text(int fd, char *data, unsigned int length);
int read_text(int fd, char *data, unsigned int length);
int read_textAll(int fd, char *data, unsigned int length);
int send_char(int fd, unsigned char data);
void flushRead2(int fd);
void flushRead(int fd);
unsigned char read_char(int fd);
uint16_t read_line(int fd, uint8_t *buff);
