#ifndef _INIT_
#define _INIT_


#define MAX_COMMANDS 8 //max arguments on one line
#define COMMAND_SIZE 64 //max length of input command

struct command_t {
	uint8_t type; //i.e. G or M
	double  value; //string value associated
	//struct command_t *next;
};
typedef struct command_t *command;

struct FloatPoint {
  float x;
  float y;
  float z;
};
FloatPoint zeros = {0.0,0.0,0.0};

struct axis_t {
  uint8_t step_pin;
  uint8_t direct_step_pin;
  uint8_t min_pin;
  uint8_t max_pin;
  float current_units;
  float target_units;
  float delta_units;
  uint32_t delta_steps;
  uint16_t timePerStep;
  int8_t direction; //FORWARD or BACKWARD
  uint32_t nextEvent;
};
typedef struct axis_t *axis;

#define FORWARD 1
#define BACKWARD -1

//misc util functions
#define SBI(port,pin) port |= _BV(pin);
#define CBI(port,pin) port &= ~_BV(pin);
#define error(x) Serial.println(x)
#define debug(x) Serial.println(x)
#define debug2(x,y) Serial.print(x);Serial.println(y)
#define debug3(x,y,z) Serial.print(x);Serial.println(y,z)
#define disable_steppers() digitalWrite(ENABLE, HIGH)
#define enable_steppers()  digitalWrite(ENABLE, LOW)
#define intDisable()      ({ uint8_t sreg = SREG; cli(); sreg; })
#define intRestore(sreg)  SREG = sreg 
#define myMicros() (micros() >> 3)

//#define X_STEPS_PER_INCH 416.772354
//#define X_STEPS_PER_MM   16.4083604

// define the parameters of our machine.
#define X_STEPS_PER_INCH 4064.0
#define X_STEPS_PER_MM   160.0

#define Y_STEPS_PER_INCH 4064.0
#define Y_STEPS_PER_MM   160.0

#define Z_STEPS_PER_INCH 4064.0
#define Z_STEPS_PER_MM   160.0

//our maximum feedrates
#define MAX_X_FEEDRATE 15.0
#define MAX_Y_FEEDRATE 15.0
#define MAX_Z_FEEDRATE 13.0

// Units in curve section
#define CURVE_SECTION_INCHES 0.019685
#define CURVE_SECTION_MM 0.5

//default stepper mode
#define DEFAULT_STEP sixteenth

//available stepper modes
#define full 1
#define half 2
#define quarter 4
#define eighth 8
#define sixteenth 16




/****************************************************************************************
* digital i/o pin assignment

****************************************************************************************/
//my config
#define ENABLE 7 //low enabled
#define MS1  6
#define MS2 5
#define MS3 2
#define RST 3 
#define SLP 3
#define STEP_X 11
#define STEP_Y 9
#define STEP_Z 10
#define DIR_X 12
#define DIR_Y 8
#define DIR_Z 13
#define MOTOR_PIN 4

/* specify the port and pin numbers - you need to look this up 
** on the schematic for the particular arduino */
#define _STEP_PORT PORTB
#define _STEP_DDR  DDRB
#define _STEP_X    (1<<3)
#define _STEP_Y    (1<<1)
#define _STEP_Z    (1<<2)


// specify min-max sense pins or 0 if not used
// specify if the pin is to to detect a switch closing when
// the signal is high using the syntax
// #define MIN_X 12 | ACTIVE_HIGH
// or to sense a low signal (preferred!!!)
// #define MIN_Y 13 | ACTIVE_LOW
// active low is prefered as it will cause the AVR to use it's internal pullups to 
// avoid bounce on the line.  If you want active_high, then you must add external pulldowns
// to avoid false signals.
#define ACTIVE_HIGH _BV(7)
#define ACTIVE_LOW  _BV(6)
#define MIN_X 0
#define MAX_X 0
#define MIN_Y 0
#define MAX_Y 0
#define MIN_Z 0
#define MAX_Z 0




#endif
