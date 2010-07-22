void motor_init(void) {
  pinMode(MOTOR_PIN, OUTPUT);
}

void motor_on(void) {
  digitalWrite(MOTOR_PIN, HIGH); 
}

void motor_off(void) {
  digitalWrite(MOTOR_PIN, LOW);
}
