#include "UsbKeyboard.h" 
#include <util/delay.h> 
 
#define BUTTON_PIN 10 
 
String mesaj; 
 
void setup() {   
  pinMode(BUTTON_PIN, INPUT);  
  TIMSK0&=!(1<<TOIE0);
  } 
 
void loop() {
  UsbKeyboard.update();
  if (digitalRead(BUTTON_PIN) == 0) {
    mesaj = "ING. POPESCU ION";
    KeyStrokeAlpNum(mesaj);
    mesaj = "DIRECTOR TEHNIC EMBEDDED DEVICES SRL";
    KeyStrokeAlpNum(mesaj);
    mesaj = "MOBIL 0789.000.233";
    KeyStrokeAlpNum(mesaj);
    mesaj = "FIX 021.456.30.03";
    KeyStrokeAlpNum(mesaj);
    UsbKeyboard.sendKeyStroke(KEY_ENTER);
    _delay_ms(200);
    } 
  }
  
void KeyStrokeAlpNum(String Sc){
  int sPoint = Sc.length();         
  for (int x = 0; x < sPoint; x++){
    int y = x + 1;
    if (Sc.substring(x, y) == "H"){ 
      UsbKeyboard.sendKeyStroke(KEY_H, MOD_SHIFT_LEFT);
    }
    else if (Sc.substring(x, y) == "L"){
      UsbKeyboard.sendKeyStroke(KEY_L, MOD_SHIFT_LEFT);

          }

       else if (Sc.substring(x, y) == " "){

          UsbKeyboard.sendKeyStroke(KEY_SPACE);

          }

       else if (Sc.substring(x, y) == "1"){

          UsbKeyboard.sendKeyStroke(KEY_1);

          }

       else if (Sc.substring(x, y) == "2"){

          UsbKeyboard.sendKeyStroke(KEY_2);

          }

       else if (Sc.substring(x, y) == "3"){

          UsbKeyboard.sendKeyStroke(KEY_3);

          }

       else if (Sc.substring(x, y) == "4"){

          UsbKeyboard.sendKeyStroke(KEY_4);

          }

       else if (Sc.substring(x, y) == "5"){

          UsbKeyboard.sendKeyStroke(KEY_5);

          }

       else if (Sc.substring(x, y) == "6"){

          UsbKeyboard.sendKeyStroke(KEY_6);

          }

       else if (Sc.substring(x, y) == "7"){

          UsbKeyboard.sendKeyStroke(KEY_7);

          }

       else if (Sc.substring(x, y) == "8"){

          UsbKeyboard.sendKeyStroke(KEY_8);

          }

       else if (Sc.substring(x, y) == "9"){

          UsbKeyboard.sendKeyStroke(KEY_9);

          }

       else if (Sc.substring(x, y) == "0"){

          UsbKeyboard.sendKeyStroke(KEY_0);

          }

       else if (Sc.substring(x, y) == "."){

          UsbKeyboard.sendKeyStroke(55);

          }

       else if (Sc.substring(x, y) == "-"){

          UsbKeyboard.sendKeyStroke(45);

          }

       else if (Sc.substring(x, y) == "A"){

          UsbKeyboard.sendKeyStroke(KEY_A, MOD_SHIFT_LEFT);

          }

       else if (Sc.substring(x, y) == "B"){

          UsbKeyboard.sendKeyStroke(KEY_B, MOD_SHIFT_LEFT);

          }

       else if (Sc.substring(x, y) == "C"){

          UsbKeyboard.sendKeyStroke(KEY_C, MOD_SHIFT_LEFT);

          }

       else if (Sc.substring(x, y) == "D"){

          UsbKeyboard.sendKeyStroke(KEY_D, MOD_SHIFT_LEFT);

          }

       else if (Sc.substring(x, y) == "E"){

          UsbKeyboard.sendKeyStroke(KEY_E, MOD_SHIFT_LEFT);

          }

       else if (Sc.substring(x, y) == "F"){

          UsbKeyboard.sendKeyStroke(KEY_F, MOD_SHIFT_LEFT);

          }

       else if (Sc.substring(x, y) == "G"){

          UsbKeyboard.sendKeyStroke(KEY_G, MOD_SHIFT_LEFT);

          }

       else if (Sc.substring(x, y) == "I"){

          UsbKeyboard.sendKeyStroke(KEY_I, MOD_SHIFT_LEFT);

          }

       else if (Sc.substring(x, y) == "J"){

          UsbKeyboard.sendKeyStroke(KEY_J, MOD_SHIFT_LEFT);

          }

       else if (Sc.substring(x, y) == "K"){

          UsbKeyboard.sendKeyStroke(KEY_K, MOD_SHIFT_LEFT);

          }

       else if (Sc.substring(x, y) == "M"){

          UsbKeyboard.sendKeyStroke(KEY_M, MOD_SHIFT_LEFT);

          }

       else if (Sc.substring(x, y) == "N"){

          UsbKeyboard.sendKeyStroke(KEY_N, MOD_SHIFT_LEFT);

          }

       else if (Sc.substring(x, y) == "O"){

          UsbKeyboard.sendKeyStroke(KEY_O, MOD_SHIFT_LEFT);

          }

       else if (Sc.substring(x, y) == "P"){

          UsbKeyboard.sendKeyStroke(KEY_P, MOD_SHIFT_LEFT);

          }

       else if (Sc.substring(x, y) == "Q"){

          UsbKeyboard.sendKeyStroke(KEY_Q, MOD_SHIFT_LEFT);

          }

       else if (Sc.substring(x, y) == "R"){

          UsbKeyboard.sendKeyStroke(KEY_R, MOD_SHIFT_LEFT);

          }

       else if (Sc.substring(x, y) == "S"){

          UsbKeyboard.sendKeyStroke(KEY_S, MOD_SHIFT_LEFT);

          }

       else if (Sc.substring(x, y) == "T"){

          UsbKeyboard.sendKeyStroke(KEY_T, MOD_SHIFT_LEFT);

          }

       else if (Sc.substring(x, y) == "U"){

          UsbKeyboard.sendKeyStroke(KEY_U, MOD_SHIFT_LEFT);

          }

       else if (Sc.substring(x, y) == "V"){

          UsbKeyboard.sendKeyStroke(KEY_V, MOD_SHIFT_LEFT);

          }

       else if (Sc.substring(x, y) == "W"){

          UsbKeyboard.sendKeyStroke(KEY_W, MOD_SHIFT_LEFT);

          }

       else if (Sc.substring(x, y) == "X"){

          UsbKeyboard.sendKeyStroke(KEY_X, MOD_SHIFT_LEFT);

          }

       else if (Sc.substring(x, y) == "Y"){

          UsbKeyboard.sendKeyStroke(KEY_Y, MOD_SHIFT_LEFT);

          }

       else if (Sc.substring(x, y) == "Z"){

          UsbKeyboard.sendKeyStroke(KEY_Z, MOD_SHIFT_LEFT);

          }

      }

      UsbKeyboard.sendKeyStroke(KEY_ENTER);

}
