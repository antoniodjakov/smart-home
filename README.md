# smart-home
Smart Home Android application built with Kotlin for Sonoff Basic WiFi relay controllers with ESPEasy firmware.

Download on PlayStore: [https://play.google.com/store/apps/details?id=mk.djakov.smarthome]



Devices: Sonoff Basic [https://www.aliexpress.com/item/4000390205431.html?spm=a2g0s.9042311.0.0.23f14c4dFCBUYP]

Firmware: ESPEasy build no.R120 [http://www.letscontrolit.com/downloads/ESPEasy_R120.zip]


Flashing Sonoff Basic tutorials: 

    1. [https://randomnerdtutorials.com/sonoff-basic-switch-esp-easy-firmware-node-red],
    2. [https://www.youtube.com/watch?v=fN_QKOWvG1s]


HTTP Commands:

    Relay on -> GET  [IP]/control?cmd=GPIO,[GPIO],1

    Relay off -> GET  [IP]/control?cmd=GPIO,[GPIO],0

    Relay status -> GET  [IP]/control?cmd=status,gpio,[GPIO]
    
    Pulse -> GET [IP]/control?cmd=Pulse,[GPIO],[state],[duration]

    LongPulse -> GET [IP]/control?cmd=LongPulse,[GPIO],[state],[duration]

    LongPulse_mS -> GET [IP]/control?cmd=Pulse,[GPIO],[state],[duration]
