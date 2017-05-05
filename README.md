# tinnitus-attenuator

Disclaimer
----------
I am not a doctor or a neurologist and I developed this program because I have tinnitus. 
As you can read in: Where does this program come from?, I read a research article that reported positive results for people which tinnitus frequency was < 8Khz.

IF YOU USE THIS PROGRAM, USE IT AT YOUR OWN RISK.

Do not listen the sounds too loud.

What is it?
-----------
This is a program that will filter the frequencies between which a tinnitus ring was detected.
To filter the sound of the computer, a band stop Butterworth Filter is used.
The filter used is from the library: dsp-collection.jar (http://www.source-code.biz/dsp/java/)

Where does this program come from?
----------------------------------
This program was inspired by the research: Short and Intense Tailor-Made Notched Music Training against Tinnitus: The Tinnitus Frequency Matters.

Link: http://journals.plos.org/plosone/article?id=10.1371/journal.pone.0024685#pone-0024685-g005

How to use it?
--------------
To use this program, you need to find first the frequency of your tinnitus tone.
With this information you can set the values required in the class TinnituesFrequencies.

To find your tinnitus frequency, use this website: http://www.szynalski.com/tone-generator/
The referred website has also a nice guide on how you can find your tinnitus frequency.

Because Java was not enough to route the audio of your computer, you need a third party program that does that. For mac you can use SoundFlower (currently tested), for windows you could use Asio4All though this was not tested yet.

Once installed the audio router, set in your computer as the default audio device to be used by it.


