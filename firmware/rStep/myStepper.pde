
void myStepper_init(void) {
  //Control Pins
  pinMode(MS1, OUTPUT);
  pinMode(MS2, OUTPUT);
  pinMode(MS3, OUTPUT);
  pinMode(RST, OUTPUT);
  pinMode(SLP, OUTPUT);
  pinMode(ENABLE, OUTPUT);
  pinMode(STEP_X, OUTPUT); 
  pinMode(STEP_Y, OUTPUT); 
  pinMode(STEP_Z, OUTPUT);
  pinMode(DIR_X, OUTPUT); 
  pinMode(DIR_Y, OUTPUT); 
  pinMode(DIR_Z, OUTPUT);
  
  //deal with limit switches
  limitConfig(MIN_X);
  limitConfig(MAX_X);
  limitConfig(MIN_Y);
  limitConfig(MAX_Y);
  limitConfig(MIN_Z);
  limitConfig(MAX_Z);

  //set pin values to stop PWM
  digitalWrite(STEP_X, LOW); 
  digitalWrite(STEP_Y, LOW); 
  digitalWrite(STEP_Z, LOW);
  digitalWrite(DIR_X, LOW); 
  digitalWrite(DIR_Y, LOW); 
  digitalWrite(DIR_Z, LOW);

  //reset and leave on but dissabled
  disable_steppers(); 
  setStep(DEFAULT_STEP);
  myStepper_reset();
}

/* configure limit switches */
void limitConfig(uint8_t pin) {
  uint8_t p;
  p = (pin & ~(ACTIVE_HIGH | ACTIVE_LOW));
  if (p) pinMode(p, INPUT); //set to input if it's to be used
  if (pin & ACTIVE_LOW) digitalWrite(p, HIGH); //turn on internal pullup if sensing active high
  else digitalWrite(p, LOW); // turn off pullup
}

/* if limit switches are used, detect if they are activated*/
bool can_move(axis a) {
  uint8_t p;
  p = (a->min_pin & ~(ACTIVE_HIGH | ACTIVE_LOW));
  if ((a->min_pin & ACTIVE_HIGH) && digitalRead(p)) return false;
  if ((a->min_pin & ACTIVE_LOW)  &&!digitalRead(p)) return false;
  p = (a->max_pin & ~(ACTIVE_HIGH | ACTIVE_LOW));
  if ((a->max_pin & ACTIVE_HIGH) && digitalRead(p)) return false;
  if ((a->max_pin & ACTIVE_LOW)  &&!digitalRead(p)) return false;
  return true;
}
  

void setStep(uint8_t s) {
  switch (s) {
  case full:
    digitalWrite(MS1, LOW); 
    digitalWrite(MS2,LOW); 
    digitalWrite(MS3,LOW);
    break;
  case half:
    digitalWrite(MS1, HIGH); 
    digitalWrite(MS2,LOW); 
    digitalWrite(MS3,LOW);
    break;
  case quarter:
    digitalWrite(MS1, LOW); 
    digitalWrite(MS2,HIGH); 
    digitalWrite(MS3,LOW);
    break;
  case eighth:
    digitalWrite(MS1, HIGH); 
    digitalWrite(MS2,HIGH); 
    digitalWrite(MS3,LOW);
    break;
  case sixteenth:
    digitalWrite(MS1, HIGH); 
    digitalWrite(MS2,HIGH); 
    digitalWrite(MS3,HIGH);
    break;
  default:
    error("Inavlid stepType");
    break;
  }
}


void myStepper_off(void) {
  digitalWrite(RST, LOW);
  digitalWrite(SLP, LOW);
}

void myStepper_on(void) {
  digitalWrite(RST, HIGH);
  digitalWrite(SLP, HIGH);
  delay(250); //wait a bit
}

void myStepper_reset(void) {
  myStepper_off();
  delay(250);
  myStepper_on();
}

