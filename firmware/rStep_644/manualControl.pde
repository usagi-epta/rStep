#include "_init.h"

// manually perform one step.  
// index is {XAXIS YAXIS or ZAXIS}
// dir is FORWARD or BACKWARD
boolean manual_step(uint8_t index, uint8_t dir) {
  axis a = axis_array[index];

  switch(index) {
  case 0: 
    digitalWrite(DIR_X, (dir==FORWARD) ? (config.dir & 0x01) : !(config.dir & 0x01)); 
    break;
  case 1: 
    digitalWrite(DIR_Y, (dir==FORWARD) ? (config.dir & 0x02) : !(config.dir & 0x02)); 
    break;
  case 2: 
    digitalWrite(DIR_Z, (dir==FORWARD) ? (config.dir & 0x04) : !(config.dir & 0x04)); 
    break;
  }

  if (can_move(a)) {
    _STEP_PORT |= a->direct_step_pin;
    //need to wait 1uS
    if (F_CPU == 20000000) {
      //additional delay if running at 20Mhz
      __asm__("nop\n\t""nop\n\t""nop\n\t""nop\n\t""nop\n\t""nop\n\t");
    }
    __asm__("nop\n\t""nop\n\t""nop\n\t""nop\n\t""nop\n\t""nop\n\t");
    __asm__("nop\n\t""nop\n\t""nop\n\t""nop\n\t""nop\n\t""nop\n\t""nop\n\t");
    _STEP_PORT &= ~a->direct_step_pin;
    return true;
  } 

  return false;
}



