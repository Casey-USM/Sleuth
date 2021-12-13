# Sleuth
Final for COS 398: A program that connects to a host's webcam and send images back to the client

The intent of this program is security research. Please don't use it for bad things.

# Running the program
The server and client program were written in the Eclipse IDE. Running them from within Eclipse should work fine.
You will need OpenCV for Java set up properly. From there you may run the server program, or if so desired, export a .jar file.

# Research
I started by looking up how other programs that "hack" webcams work. If Google is to be believed, it seems the most popular way to gain discrete access to another computer's webcam is through use of Metasploit and Meterpeter.
I searched for other more specific hacks and I did find one that used primarily php to generate a malicious link, but that is a bit out of my wheelhouse.
To get some more information on how meterpeter works, I cloned the repository for it and started looking at the code. The webcam module for it seems to be written in C++, but my base understanding of C proved useful in understanding what it does.
It seems to act as any other program that would access a webcam would. It starts threads to manage it, locks the camera for use, and then sends pictures or video to the client program.
For this project I will mimic that behavior using the language I know best: Java.

Links to articles:
https://null-byte.wonderhowto.com/forum/argo-hack-webcams-and-cameras-0207991/
https://www.hackers-arise.com/how-to-hack-web-cams

The PHP repo:
https://github.com/adolabsnet/shasnap

# The good, the bad, and the libraries
Java has no naitive way to access webcams, so about half the battle was finding a library I could work with that would provide the access I needed. The first one I tried can be found here: https://github.com/sarxos/webcam-capture
At first I tried to read through it and try to figure out how to make my own way of accessing the webcam. After several hours I gave up on that idea. I used some example code and got it to compile and run, but the resulting image was just a small black square.

Next I tried to set up a Maven project in Eclipse that used JavaCV. It proved to be very troublesome, and the sample code would not work.
Some of the issues with JavaCV cited problems with OpenCV, so I tried pivoting to that using the same Maven setup, but that didn't work either.

Then I started to consider using C# instead. The Universal Windows Platform, which provides webcam access, isn't supported in VSCode, so I switched to Visual Studio Community 2019. The basic applications that use UWP were far too complex for the scope of this project, and there didn't appear to be a way to make a command line application that would still allow me to do what I wanted.

Finally, I gave OpenCV another look and after many hours of troubleshooting I was able to get it working. The bulk of the time that I've spent on this project has been in trying to get a library for webcam access working.
The version I used can be found here:
https://sourceforge.net/projects/opencvlibrary/files/4.5.4/opencv-4.5.4-openvino-dldt-2021.4.1-vc16-avx2.zip/download

Process for getting it working in Windows:
Add the .jar file in build/java to Eclipse as a user library.
Set the native library path to build/java/x64
Add build/bin to the PATH environmental variable

Even after this the test program still had problems with the image not being recognizable. This was due to the default resolution not being supported by the camera. In the code I explicitly set the resolution for the capture to what the webcam uses. If I have enough time I hope to be able to come back to the resolution issue and find a more dynamic way to handle it.

# But what about the webcam light and concurrent access?
The camera that I'm testing on, as mentioned in the project proposal, does not have a light to indicate that it is in use, so for now that particular item is a stretch goal. As for being able to access the camera while it's in use, based on my research it looks like even meterpeter locks the webcam, and I imagine finding ways around that would cause some memory access issues, so that particular part will be ignored for now.

Regarding the light, I did find a page that talks about how to disable it by modifying the driver: https://blog.erratasec.com/2013/12/how-to-disable-webcam-light-on-windows.html
The driver .dll would have to be modified so that when the camera is in use the light would not come on. This would have to be fairly targeted because you'd have to know what camera the host has and then edit/replace the driver on the machine with one that doesn't turn on the light.


