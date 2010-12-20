/*
 *  DBG.cpp
 *  
 *
 *  Created by Michael Nash on 12/19/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */
#include "DBG.h"

/*below are pin assignments for 
rStep_644...for 328 version they 
will have to be modified*/

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

uint8_t dig_pin[32] = {
	0,1,2,3,4,5,6,7,
	0,1,2,3,4,5,6,7,
	0,1,2,3,4,5,6,7,
	7,6,5,4,3,2,1,0};

//typedef uint8_t unsigned char;
//typedef unsigned int uint16_t;

DBGClass DBG;

void DBGClass::begin(long baud) {
	Serial.begin(baud);
}

void DBGClass::out(char *label, uint8_t pin) {
	Serial.print(label);
	Serial.print(": ");
	Serial.println(check(pin),DEC);
}

void DBGClass::outPin(char *label, uint8_t pin) {
	outAdv(label, pin, "OUTPUT", "INPUT");
	outAdv(label, pin, "is HIGH", "is LOW");
}

void DBGClass::outAdv(char *label, uint8_t pin, char *msg_if_true, char *msg_if_false){
	Serial.print(label);
	Serial.print(": ");
	if(check(pin)) Serial.println(msg_if_true);
	else Serial.println(msg_if_false);
}

void DBGClass::stepsize(){
	if(!check(MS1) && !check(MS1)) Serial.println("Full-step mode active");
	else if(!check(MS1) && check(MS2)) Serial.println("Half-step mode active");//verify
	else if(check(MS1) && !check(MS2)) Serial.println("Quarter-step mode active");//verify
	else if(check(MS1) && check(MS2)) Serial.println("Sixteenth-step mode active");
}

void DBGClass::steppers(){
	if(!checkDir(X_STEP)) Serial.println("Warning:  X-Step pin set as INPUT");
	if(!checkDir(Y_STEP)) Serial.println("Warning:  Y-Step pin set as INPUT");
	if(!checkDir(Z_STEP)) Serial.println("Warning:  Z-Step pin set as INPUT");
	
	if(!checkDir(X_DIR)) Serial.println("Warning:  X-Direction set as INPUT");
	if(!checkDir(Y_DIR)) Serial.println("Warning:  Y-Direction set as INPUT");
	if(!checkDir(Z_DIR)) Serial.println("Warning:  Z-Direction set as INPUT");
	
	if(!checkDir(STEP_EN)) Serial.println("Warning:  Stepper Enable Pin set as INPUT");
	if(!checkDir(STEP_RST)) Serial.println("Warning:  Stepper Reset Pin set as INPUT");
	
	if(!checkDir(MS1)) Serial.println("Warning:  MS1 set as INPUT");
	if(!checkDir(MS2)) Serial.println("Warning:  MS2 set as INPUT");
	
	if(check(STEP_EN)) Serial.println("Warning:  Stepper Enable HIGH (Stepper Enable is Active LOW)");
	if(!check(STEP_RST)) Serial.println("Warning:  Stepper Reset LOW (should be held HIGH)");
}

void DBGClass::all(){
	outAdv("IO 06", IO_06, "is HIGH", "is LOW");	
	outAdv("Spindle", SPINDLE, "is HIGH", "is LOW");	
	outAdv("Stepper Enable", STEP_EN, "is HIGH", "is LOW");	
	outAdv("Stepper Reset", STEP_RST, "is HIGH", "is LOW");	
	outAdv("Slave Select", SS, "is HIGH", "is LOW");	
	outAdv("MOSI", MOSI, "is HIGH", "is LOW");	
	outAdv("MISO", MISO, "is HIGH", "is LOW");	
	outAdv("SCK", SCK, "is HIGH", "is LOW");
	outAdv("RXD", RXD, "is HIGH", "is LOW");	
	outAdv("TXD", TXD, "is HIGH", "is LOW");	
	outAdv("Z Step", Z_STEP, "is HIGH", "is LOW");	
	outAdv("Z Dir", Z_DIR, "is HIGH", "is LOW");
	outAdv("Y Step", Y_STEP, "is HIGH", "is LOW");
	outAdv("Y Dir", Y_DIR, "is HIGH", "is LOW");
	outAdv("X Step", X_STEP, "is HIGH", "is LOW");
	outAdv("X Dir", X_DIR, "is HIGH", "is LOW");
	outAdv("MS2", MS2, "is HIGH", "is LOW");
	outAdv("MS1", MS1, "is HIGH", "is LOW");
	outAdv("TCK", TCK, "is HIGH", "is LOW");	
	outAdv("TMS", TMS, "is HIGH", "is LOW");
	outAdv("TDO", TDO, "is HIGH", "is LOW");
	outAdv("TDI", TDI, "is HIGH", "is LOW");
	outAdv("Y Limit", LIM_Y, "is HIGH", "is LOW");
	outAdv("X Limit", LIM_X, "is HIGH", "is LOW");
	outAdv("Emergency Stop", E_STOP, "is HIGH", "is LOW");
	outAdv("IO 01", IO_01, "is HIGH", "is LOW");	
	outAdv("Z Limit", IO_06, "is HIGH", "is LOW");	
	outAdv("Status LED", IO_06, "is HIGH", "is LOW");
	outAdv("IO 02", IO_02, "is HIGH", "is LOW");
	outAdv("IO 03", IO_03, "is HIGH", "is LOW");
	outAdv("IO 04", IO_04, "is HIGH", "is LOW");
	outAdv("IO 05", IO_05, "is HIGH", "is LOW");
}


/******PRIVATE******/

uint8_t DBGClass::check(int dpin){
	if(dpin <= 7){
		if(PINB&(1<<dig_pin[dpin])) return 1;
		else return 0;
	}
	else if((dpin <= 15) && (dpin >= 8)){
		if(PIND&(1<<dig_pin[dpin])) return 1;
		else return 0;
	}
	else if((dpin >= 16) && (dpin <= 23)){
		if(PINC&(1<<dig_pin[dpin])) return 1;
		else return 0;
	}
	else if((dpin >= 24) && (dpin <= 31)){ 
		if(PINA&(1<<dig_pin[dpin])) return 1;
		else return 0;
	}
	else return 0;
}

uint8_t DBGClass::checkDir(int dpin){
	if(dpin <= 7){
		if(DDRB&(1<<dig_pin[dpin])) return 1;
		else return 0;
	}
	else if((dpin <= 15) && (dpin >= 8)){
		if(DDRD&(1<<dig_pin[dpin])) return 1;
		else return 0;
	}
	else if((dpin >= 16) && (dpin <= 23)){
		if(DDRC&(1<<dig_pin[dpin])) return 1;
		else return 0;
	}
	else if((dpin >= 24) && (dpin <= 31)){ 
		if(DDRA&(1<<dig_pin[dpin])) return 1;
		else return 0;
	}
	else return 0;
}

