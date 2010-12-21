
void bzero(uint8_t *ptr, uint8_t len) {
  for (uint8_t i=0; i<len; i++) ptr[i] = 0;
}

void init_steppers(){
  //turn them off to start.
  disable_steppers();

  // setup data
  xaxis = &xaxis_data;
  yaxis = &yaxis_data;
  zaxis = &zaxis_data;

  axis_array[0] = xaxis;
  axis_array[1] = yaxis;
  axis_array[2] = zaxis;

  bzero((uint8_t*)&xaxis_data, sizeof(struct axis_t)); 
  bzero((uint8_t*)&yaxis_data, sizeof(struct axis_t)); 
  bzero((uint8_t*)&zaxis_data, sizeof(struct axis_t)); 

  // configure pins
  xaxis->step_pin = STEP_X;
  yaxis->step_pin = STEP_Y;
  zaxis->step_pin = STEP_Z;

  xaxis->minMax_pin  = MINMAX_X;
  yaxis->minMax_pin  = MINMAX_Y;
  zaxis->minMax_pin  = MINMAX_Z;

  xaxis->direct_step_pin = _STEP_X;
  yaxis->direct_step_pin = _STEP_Y;
  zaxis->direct_step_pin = _STEP_Z;

  //figure our stuff.
  calculate_deltas();
}


axis nextEvent(void) {
  if (xaxis->nextEvent < yaxis->nextEvent) {
    return (xaxis->nextEvent <= zaxis->nextEvent) ? xaxis : zaxis;
  } 
  else {
    return (yaxis->nextEvent <= zaxis->nextEvent) ? yaxis : zaxis;
  }
}

//#define dbg(x) Serial.print("DEBUG: "); Serial.println(x)
#define dbg(x)


/* reset the interal timer to zero*/
void myResetMicros(void) {
//  Serial.print("MSG ["); Serial.print(timer0_overflow_count);Serial.println("]");
  dbg("reset");
  uint8_t oldSREG = SREG;
  cli();
  timer0_overflow_count = 0;
  TCNT0 = 0;
  SREG = oldSREG;
}



void r_move(float feedrate) {
  uint32_t starttime,duration;
  float distance;
  axis a;
  uint8_t i;

  //uint8_t sreg = intDisable();

dbg("a");

  if (!feedrate ) {
    //compute max feedrate per axis
    xaxis->timePerStep = (double)(1E6*60.0) / (double)((double)config.max_feedrate.x * (double)config.steps_inch.x * (double)config.stepping);
    yaxis->timePerStep = (double)(1E6*60.0) / (double)((double)config.max_feedrate.y * (double)config.steps_inch.y * (double)config.stepping);
    zaxis->timePerStep = (double)(1E6*60.0) / (double)((double)config.max_feedrate.z * (double)config.steps_inch.z * (double)config.stepping);
  } 
  else {
    // if (feedrate > getMaxFeedrate()) feedrate = getMaxFeedrate();
    // distance / feedrate * 60000000.0 = move duration in microseconds
    distance = sqrt(xaxis->delta_units*xaxis->delta_units + 
      yaxis->delta_units*yaxis->delta_units + 
      zaxis->delta_units*zaxis->delta_units);
    duration = ((distance * 60000000.0) / feedrate); //in uS
  }
dbg("b");
  // setup axis
  for (i=0;i<3;i++) {
    a = axis_array[i];
    if (axis_array[i]->delta_steps) {
      if (feedrate) a->timePerStep = duration / axis_array[i]->delta_steps;
      a->nextEvent = (a->timePerStep>>1); //1st event happens halfway though cycle.
    } 
    else {
      a->nextEvent = 0xFFFFFFFF;
    }
  }
dbg("c");  
  myResetMicros();
  starttime = myMicros();
#ifdef REPORT_DELAY
  uint32_t nextProgressReport = millis() + REPORT_DELAY;
  uint32_t xaxis_delta_steps = xaxis->delta_steps;
  uint32_t yaxis_delta_steps = yaxis->delta_steps;
  uint32_t zaxis_delta_steps = zaxis->delta_steps;
#endif
  // start move
dbg("d");  
  while (xaxis->delta_steps || yaxis->delta_steps || zaxis->delta_steps) {
    a = nextEvent();
    while (myMicros() < (starttime + a->nextEvent) ); //wait till next action is required
    dbg("d1");
    if (can_move(a)) {
      dbg("d2");
      _STEP_PORT |= a->direct_step_pin;
      //need to wait 1uS
      if (F_CPU == 20000000) {
        //additional delay if running at 20Mhz
        __asm__("nop\n\t""nop\n\t""nop\n\t""nop\n\t""nop\n\t""nop\n\t");
      }
      __asm__("nop\n\t""nop\n\t""nop\n\t""nop\n\t""nop\n\t""nop\n\t");
      __asm__("nop\n\t""nop\n\t""nop\n\t""nop\n\t""nop\n\t""nop\n\t""nop\n\t");
      _STEP_PORT &= ~a->direct_step_pin;
    }
    dbg("d3");
    if (--a->delta_steps) {
      a->nextEvent += a->timePerStep;
    } 
    else {
      a->nextEvent = 0xFFFFFFFF; 
    }

#ifdef REPORT_DELAY
    // print the move progress each REPORT_DELAY milliseconds.
    if (!quiet && millis() > nextProgressReport) {
      MessageCoordinates(
        getLivePosition(0, xaxis_delta_steps),
        getLivePosition(1, yaxis_delta_steps),
        getLivePosition(2, zaxis_delta_steps));
      nextProgressReport += REPORT_DELAY;
    }
#endif
  }
  dbg("e");
  //we are at the target
  xaxis->current_units = xaxis->target_units;
  yaxis->current_units = yaxis->target_units;
  zaxis->current_units = zaxis->target_units;
  calculate_deltas();

#ifdef REPORT_DELAY
    // Print the move progress at the end
    if (!quiet) MessageCoordinates( xaxis->current_units, yaxis->current_units, zaxis->current_units);
#endif

  //intRestore(sreg);
}

#ifdef REPORT_DELAY
float getLivePosition(int axis, uint32_t steps) {
  return
    axis_array[axis]->current_units +
      ((axis_array[axis]->direction == FORWARD) ? 1.0 : -1.0) *
        (float)(steps - axis_array[axis]->delta_steps) / (config.stepping * _units[axis]);
}
#endif

void set_target(FloatPoint *fp){
  xaxis->target_units = fp->x;
  yaxis->target_units = fp->y;
  zaxis->target_units = fp->z;
  calculate_deltas();
}

void set_position(FloatPoint *fp){
  xaxis->current_units = fp->x;
  yaxis->current_units = fp->y;
  zaxis->current_units = fp->z;
  calculate_deltas();
}

void calculate_deltas() {
  //figure our deltas. 
  axis a;
  int i;
  for (i=0; i<3; i++) {
    a = axis_array[i];
    a->delta_units = a->target_units - a->current_units;
    a->delta_steps = (long)(_units[i]*abs(a->delta_units)*config.stepping); //XXX make x_units a vector
    a->direction = (a->delta_units < 0) ? BACKWARD : FORWARD;

    switch(i) {
    case 0: 
      digitalWrite(DIR_X, (a->direction==FORWARD) ? (config.dir & 0x01) : !(config.dir & 0x01)); 
      break;
    case 1: 
      digitalWrite(DIR_Y, (a->direction==FORWARD) ? (config.dir & 0x02) : !(config.dir & 0x02)); 
      break;
    case 2: 
      digitalWrite(DIR_Z, (a->direction==FORWARD) ? (config.dir & 0x04) : !(config.dir & 0x04)); 
      break;
    }
  }
#ifdef DEBUG
  Message3F("DeltaSteps", axis_array[0]->delta_steps, axis_array[1]->delta_steps,  axis_array[2]->delta_steps, DEC);
#endif
}


uint16_t getMaxFeedrate() {
  uint16_t temp = _getMaxFeedrate();
  return (_units[0] == config.max_feedrate.x) ? temp:(temp*25.4);
}

#define MAX_X_FEEDRATE config.max_feedrate.x
#define MAX_Y_FEEDRATE config.max_feedrate.y
#define MAX_Z_FEEDRATE config.max_feedrate.z
uint16_t _getMaxFeedrate() {
  if (!xaxis->delta_steps) {
    if (!yaxis->delta_steps) return MAX_Z_FEEDRATE;
    if (!zaxis->delta_steps) return MAX_Y_FEEDRATE;
    return min(MAX_Z_FEEDRATE, MAX_Y_FEEDRATE);
  }
  if (!yaxis->delta_steps) {
    if (!xaxis->delta_steps) return MAX_Y_FEEDRATE;
    if (!zaxis->delta_steps) return MAX_X_FEEDRATE;
    return min(MAX_X_FEEDRATE, MAX_Y_FEEDRATE);
  }
  if (!zaxis->delta_steps) {
    if (!yaxis->delta_steps) return MAX_X_FEEDRATE;
    if (!xaxis->delta_steps) return MAX_Y_FEEDRATE;
    return min(MAX_X_FEEDRATE, MAX_Y_FEEDRATE);
  }
  return min(MAX_X_FEEDRATE, min(MAX_Y_FEEDRATE, MAX_Z_FEEDRATE));
}

