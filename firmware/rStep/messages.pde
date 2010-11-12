void coordinatesMessage(float x, float y, float z) {
  Serial.print("MSG Coord(");
  Serial.print(x, 5);
  Serial.print(',');
  Serial.print(y, 5);
  Serial.print(',');
  Serial.print(z, 5);
  Serial.println(')');
}

