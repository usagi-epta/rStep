void motor_init(void) {
  pinMode(MOTOR_PIN, OUTPUT);
}

void motor_on(void) {
  analogWrite(MOTOR_PIN, config.motorSpeed);
 // digitalWrite(MOTOR_PIN, HIGH); 
}

void motor_off(void) {
  analogWrite(MOTOR_PIN, 0);
//  digitalWrite(MOTOR_PIN, LOW);
}