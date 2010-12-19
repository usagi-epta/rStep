void motor_init(void) {
  pinMode(MOTOR_PIN, OUTPUT);
}

void motor_on(void) {
  analogWrite(MOTOR_PIN, config.motorSpeed);
}

void motor_off(void) {
  digitalWrite(MOTOR_PIN, LOW);
}
