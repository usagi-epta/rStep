


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

  xaxis->min_pin  = MIN_X;
  yaxis->min_pin  = MIN_Y;
  zaxis->min_pin  = MIN_Z;

  xaxis->max_pin  = MAX_X;
  yaxis->max_pin  = MAX_Y;
  zaxis->max_pin  = MAX_Z;

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

/*
void fast_move(void) {
 axis a;
 uint8_t i;
 uint32_t starttime;
 
 xaxis->timePerStep = (1E6*60.0) / (MAX_X_FEEDRATE * X_STEPS_PER_INCH * stepping);
 yaxis->timePerStep = (1E6*60.0) / (MAX_Y_FEEDRATE * Y_STEPS_PER_INCH * stepping);
 zaxis->timePerStep = (1E6*60.0) / (MAX_Z_FEEDRATE * Z_STEPS_PER_INCH * stepping);
 
 // setup axis
 for (i=0;i<3;i++) {
 a = axis_array[i];
 if (axis_array[i]->delta_steps) {
 a->nextEvent = a->timePerStep; //1st event happens halfway though cycle.
 } 
 else {
 a->nextEvent = 0xFFFFFFFF;
 }
 }
 // start move
 starttime = micros();
 while (xaxis->delta_steps || yaxis->delta_steps || zaxis->delta_steps) {
 a = nextEvent();
 while (micros() < (starttime + a->nextEvent) ); //wait till next action is required
 if (can_move(a)) {
 _STEP_PORT |= a->direct_step_pin;
 //need to wait 1uS
 __asm__("nop\n\t""nop\n\t""nop\n\t""nop\n\t""nop\n\t""nop\n\t");
 __asm__("nop\n\t""nop\n\t""nop\n\t""nop\n\t""nop\n\t""nop\n\t""nop\n\t");
 _STEP_PORT &= ~a->direct_step_pin;
 }
 
 if (--a->delta_steps) {
 a->nextEvent += a->timePerStep;
 } 
 else {
 a->nextEvent = 0xFFFFFFFF; 
 }
 }
 
 //we are at the target
 xaxis->current_units = xaxis->target_units;
 yaxis->current_units = yaxis->target_units;
 zaxis->current_units = zaxis->target_units;
 calculate_deltas();
 }
 */



void r_move(float feedrate) {
  uint32_t starttime,duration;
  float distance;
  axis a;
  uint8_t i;

  //uint8_t sreg = intDisable();

  if (!feedrate ) {
    xaxis->timePerStep = (1E6*60.0) / (MAX_X_FEEDRATE * X_STEPS_PER_INCH * stepping);
    yaxis->timePerStep = (1E6*60.0) / (MAX_Y_FEEDRATE * Y_STEPS_PER_INCH * stepping);
    zaxis->timePerStep = (1E6*60.0) / (MAX_Z_FEEDRATE * Z_STEPS_PER_INCH * stepping);
  } 
  else {
    // if (feedrate > getMaxFeedrate()) feedrate = getMaxFeedrate();
    // distance / feedrate * 60000000.0 = move duration in microseconds
    distance = sqrt(xaxis->delta_units*xaxis->delta_units + 
      yaxis->delta_units*yaxis->delta_units + 
      zaxis->delta_units*zaxis->delta_units);
    duration = ((distance * 60000000.0) / feedrate); //in uS
  }

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

  starttime = myMicros();  
  // start move
  while (xaxis->delta_steps || yaxis->delta_steps || zaxis->delta_steps) {
    a = nextEvent();
    while (myMicros() < (starttime + a->nextEvent) ); //wait till next action is required
    if (can_move(a)) {
      _STEP_PORT |= a->direct_step_pin;
      //need to wait 1uS
      __asm__("nop\n\t""nop\n\t""nop\n\t""nop\n\t""nop\n\t""nop\n\t");
      __asm__("nop\n\t""nop\n\t""nop\n\t""nop\n\t""nop\n\t""nop\n\t""nop\n\t");
      _STEP_PORT &= ~a->direct_step_pin;
    }
    if (--a->delta_steps) {
      a->nextEvent += a->timePerStep;
    } 
    else {
      a->nextEvent = 0xFFFFFFFF; 
    }
  }

  //we are at the target
  xaxis->current_units = xaxis->target_units;
  yaxis->current_units = yaxis->target_units;
  zaxis->current_units = zaxis->target_units;
  calculate_deltas();

  //Serial.println("DDA_move finished");
  //intRestore(sreg);
}

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


long to_steps(float steps_per_unit, float units){
  return steps_per_unit * units * stepping;
}

void calculate_deltas() {
  //figure our deltas. 
  axis a;
  int i;

  for (i=0; i<3; i++) {
    a = axis_array[i];
    a->delta_units = a->target_units - a->current_units;
    a->delta_steps = to_steps(_units[i], abs(a->delta_units)); //XXX make x_units a vector
    a->direction = (a->delta_units < 0) ? BACKWARD : FORWARD;

    switch(i) {
    case 0: 
      digitalWrite(DIR_X, (a->direction==FORWARD) ? LOW : HIGH); 
      break;
    case 1: 
      digitalWrite(DIR_Y, (a->direction==FORWARD) ? LOW : HIGH); 
      break;
    case 2: 
      digitalWrite(DIR_Z, (a->direction==FORWARD) ? HIGH : LOW); 
      break;
    }
  }
}


uint16_t getMaxFeedrate() {
  uint16_t temp = _getMaxFeedrate();
  return (_units[0] == X_STEPS_PER_MM) ? (temp*25.4) : temp;
}

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





