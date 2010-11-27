#ifndef _INIT_
#define _INIT_

// rStep compilation configuration
// PROGRESS_DELAY: The average time between two report messages (in milliseconds)
// uncomment to enable this feature
//#define REPORT_DELAY 5000

//constants
#define MAX_COMMANDS 8 //max arguments on one line
#define COMMAND_SIZE 64 //max length of input command
//default stepper mode
#define DEFAULT_STEP sixteenth
//available stepper modes
#define full 1
#define half 2
#define quarter 4
#define eighth 8
#define sixteenth 16
//misc definitions
#define FORWARD 1
#define BACKWARD -1
#define CHAN_X 0
#define CHAN_Y 1
#define CHAN_Z 2
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
//pin definitions
/****************************************************************************************/
//my config
#define ENABLE 2 //low enabled
#define MS1 17
#define MS2 16
#define RST 3 
#define SLP 3
#define STEP_X 14
#define STEP_Y 12
#define STEP_Z 10
#define DIR_X 15
#define DIR_Y 13
#define DIR_Z 11
#define MOTOR_PIN 1

#define LED1 27
#define LED2 30

#define SCK 7
#define MOSI 5
#define MISO 6
#define SS 4
#define MSP4351_CS 4

/* specify the port and pin numbers - you need to look this up 
** on the schematic for the particular arduino */
#define _STEP_PORT PORTD
#define _STEP_DDR  DDRD
#define _STEP_X    (1<<6)
#define _STEP_Y    (1<<4)
#define _STEP_Z    (1<<2)


// specify min-max sense pins or 0 if not used
// active low is prefered as it will cause the AVR to use it's internal pullups to 
// avoid bounce on the line.  If you want active_high, then you must add external pulldowns
// to avoid false signals.
#define MINMAX_X 23
#define MINMAX_Y 22
#define MINMAX_Z 26

// STRUCTURES
struct u16_t {
  uint16_t x;
  uint16_t y;
  uint16_t z;
};
struct u8_t {
  uint8_t x;
  uint8_t y;
  uint8_t z;
};
struct FloatPoint {
  float x;
  float y;
  float z;
};

struct axis_t {
  uint8_t step_pin;
  uint8_t direct_step_pin;
  uint8_t minMax_pin;

  float current_units; // the current position on the axis
  float target_units;  // the position to reach at the end of the current move
  float delta_units;   // the relative position of target_units from the current_units position 
                       // delta_units is updated by the calculate_deltas() function

  uint32_t delta_steps; // the absolute number of steps (or µsteps) to travel through delta_units
                        // the initial value is computed in the calculate_deltas() function,
                        // then it's decreased while moving (in the r_move() function)
  uint16_t timePerStep; // the number of µsec to achieve a move on this axis
                        // this is computed in the r_move()
                        
  int8_t direction; // FORWARD or BACKWARD
                    // equals BACKWARD, if delta_units < 0, or FORWARD otherwise
  
  uint32_t nextEvent; // delay (in µsec) to the next step on this axis (computed in the r_move() function)
};
typedef struct axis_t *axis;

struct command_t {
	uint8_t type; //i.e. G or M
	double  value; //string value associated
	//struct command_t *next;
};
typedef struct command_t *command;

typedef struct config_t {
  struct u16_t steps_inch;   // the number of steps per inch, set by M101
  struct u8_t  max_feedrate; // the max feedrate, set by M102
  struct u8_t  current;      // the stepper motors current (in amps), set by M100
  uint8_t      stepping;     // the motors stepping (options are 1, 2, 4, 16), set by M103 Sx
  bool         abs_mode;     // set by G90 or G91
  uint8_t      dir;          // set by M104
  uint8_t      motorSpeed;   // the PWM duty cycle of the stepper motors when they're on (allowed values are 1-255), set by M105 Sx
};

/* LED */

#define LED1_ON() digitalWrite(LED1,LOW);
#define LED1_OFF() digitalWrite(LED1,HIGH);
#define LED2_ON() digitalWrite(LED2,LOW);
#define LED2_OFF() digitalWrite(LED2,HIGH);

#endif
