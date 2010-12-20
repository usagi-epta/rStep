/*DBG by Michael Nash*/

#ifndef DBG_H
#define DBG_H

#include "WProgram.h"

#define IO_06		0
#define SPINDLE	1
#define STEP_EN	2
#define STEP_RST	3
#define SS		4
#define MOSI		5
#define MISO		6
#define SCK		7
#define RXD		8
#define TXD		9
#define Z_STEP		10
#define Z_DIR		11
#define Y_STEP		12
#define Y_DIR		13
#define X_STEP		14
#define X_DIR		15
#define MS2		16
#define MS1		17
#define TCK		18
#define TMS		19
#define TDO		20
#define TDI		21
#define LIM_Y		22
#define LIM_X		23
#define E_STOP		24
#define IO_01		25
#define LIM_Z		26
#define STAT_LED	27
#define IO_02		28
#define IO_03		29
#define IO_04		30
#define IO_05		31


//typedef unsigned char uint8_t;
//typedef unsigned int uint16_t;

class DBGClass{
public:
      void begin(long baud);
      void out(char *label, uint8_t pin) ;
      void outPin(char *label, uint8_t pin) ;
      void outAdv(char *label, uint8_t pin, char *msg_if_true, char *msg_if_false);
      void stepsize();
      void steppers();
      void all();
      uint8_t check(int dpin);
      uint8_t checkDir(int dpin);
      
private:

	
};

extern DBGClass DBG;

#endif

