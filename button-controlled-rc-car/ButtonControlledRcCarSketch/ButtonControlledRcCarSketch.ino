#include <SoftwareSerial.h>
SoftwareSerial BT(10, 11);

#define LEFT_F 4
#define LEFT_B 5
#define LEFT_E 3
#define RIGHT_F 6
#define RIGHT_B 8
#define RIGHT_E 9

int maxSpeed = 50; // from 0 to 100

void setup() {
  Serial.begin(9600);
  BT.begin(9600);
  pinMode(LEFT_F, OUTPUT);
  pinMode(LEFT_B, OUTPUT);
  pinMode(RIGHT_F, OUTPUT);
  pinMode(RIGHT_B, OUTPUT);

  digitalWrite(LEFT_F, LOW);
  digitalWrite(LEFT_B, LOW);
  digitalWrite(RIGHT_F, LOW);
  digitalWrite(RIGHT_B, LOW);  
}

void loop() {
  if (BT.available() > 0) {
    char val = BT.read();
    if (val == '1') {
      forward();
    } else if (val == '2') {
      backward();
    } else if (val == '3') {
      radialLeft();
    } else if (val == '4') {
      radialRight();
    } else if (val == '0') {
      stopCar();
    }
  }
}

void motorWrite(int a, int b, int c, int d) {
  digitalWrite(LEFT_F, a);
  digitalWrite(LEFT_B, b);
  digitalWrite(RIGHT_F, c);
  digitalWrite(RIGHT_B, d);
  analogWrite(LEFT_E, map(maxSpeed, 0, 100, 0, 255));
  analogWrite(RIGHT_E, map(maxSpeed, 0, 100, 0, 255));
}

void forward() {
  motorWrite(HIGH, LOW, HIGH, LOW);
}

void backward() {
  motorWrite(LOW, HIGH, LOW, HIGH);
}

void radialLeft() {
  motorWrite(LOW, LOW, HIGH, LOW);
}

void radialRight() {
  motorWrite(HIGH, LOW, LOW, LOW);
}

void axialLeft() {
  motorWrite(LOW, HIGH, HIGH, LOW);
}

void axialRight() {
  motorWrite(HIGH, LOW, LOW, HIGH);
}

void stopCar() {
  motorWrite(LOW, LOW, LOW, LOW);  
}
