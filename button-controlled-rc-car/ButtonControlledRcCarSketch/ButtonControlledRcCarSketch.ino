#include <SoftwareSerial.h>
SoftwareSerial BT(10, 11);

#define LEFT_F 3
#define LEFT_B 5
#define RIGHT_F 6
#define RIGHT_B 9

int maxSpeed = 50; // from 0 to 100

void setup() {
  pinMode(LEFT_F, OUTPUT);
  pinMode(LEFT_B, OUTPUT);
  pinMode(RIGHT_F, OUTPUT);
  pinMode(RIGHT_B, OUTPUT);
  
  Serial.begin(9600);
  BT.begin(9600);
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
  a = map(a, 0, 100, 0, 255);
  b = map(b, 0, 100, 0, 255);
  c = map(c, 0, 100, 0, 255);
  d = map(d, 0, 100, 0, 255);
  analogWrite(LEFT_F, a);
  analogWrite(LEFT_B, b);
  analogWrite(RIGHT_F, c);
  analogWrite(RIGHT_B, d);
}

void forward() {
  motorWrite(maxSpeed, 0, maxSpeed, 0);
}

void backward() {
  motorWrite(0, maxSpeed, 0, maxSpeed);
}

void radialLeft() {
  motorWrite(0, 0, maxSpeed, 0);
}

void radialRight() {
  motorWrite(maxSpeed, 0, 0, 0);
}

void axialLeft() {
  motorWrite(0, maxSpeed, maxSpeed, 0);
}

void axialRight() {
  motorWrite(maxSpeed, 0, 0, maxSpeed);
}

void stopCar() {
  motorWrite(0, 0, 0, 0);  
}
