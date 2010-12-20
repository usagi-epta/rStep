#include <EEPROM.h>
#include <inttypes.h>
#include "_init.h"
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include "messages.h"

bool     quiet;
uint8_t  command_word[COMMAND_SIZE];
uint8_t  serial_count=0;
uint16_t no_data = 0;
axis     axis_array[3];
struct   config_t config;
FloatPoint zeros = {0.0,0.0,0.0};
float feedrate;

struct axis_t xaxis_data;
struct axis_t yaxis_data;
struct axis_t zaxis_data;

axis xaxis;
axis yaxis;
axis zaxis;

void setup() {
  quiet = true;
  Serial.begin(9600);
  pinMode(LED1, OUTPUT);// init led
  pinMode(LED2, OUTPUT);// init led
  
  LED1_ON();
  LED2_ON();
  //init code
  config_read();
  mcp4351_init();
  myStepper_init();
  motor_init();
  init_steppers();
  init_process_string();
  //increase clock resolution

  TCCR0B &= ~_BV(CS00); //for ATmega168!! XXX: this will mess up millis,micros,delay,delayMicroseconds 
  
  //set current system feedrate
  feedrate = getMaxFeedrate();
  
  //default configuration
  process_string((uint8_t*)"G21"); //default in mm
  quiet = false;
  Serial.print("start (v");
  Serial.print("$Rev$ @ ");
  Serial.print(F_CPU);
  Serial.println("Hz)");
  LED2_OFF();
}

void init_process_string() {
  for (byte i=0; i<COMMAND_SIZE; i++) command_word[i] = 0;
  serial_count = 0;
}


void loop() {
  uint8_t c;
  uint16_t newKeys;

  //read in characters if we got them.
  if (Serial.available() > 0)	{
    c = (uint8_t)Serial.read();
    no_data = 0;
    command_word[serial_count++] = c;
  } 
  else {
    no_data++;
    delayMicroseconds(100);
  }

  //if theres a pause or we got a real command, do it
  if (serial_count && (c == '\n' || no_data > 100)) {
    LED1_OFF();
    command_word[serial_count] = 0; //change \n to null terminator
    process_string(command_word); //do
    init_process_string(); //clear
    LED1_ON();
  }

  //no data?  turn off steppers
  if (no_data > 1000)  {
    disable_steppers();
  }
}
//foo


