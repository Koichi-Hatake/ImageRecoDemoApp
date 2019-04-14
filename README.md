# ImageRecoDemoApp
This is a demo application of image recognition by neural network.

## Description
This is easy sample Android application to learn first step of image classification of machine learning by Neural Network Libraries (https://nnabla.org/). The image classification can be easily performed on photo take by yourself.

## Screen shot
![ImageRecoDemo_Screen](https://user-images.githubusercontent.com/45664722/56088530-dd87b880-5ebd-11e9-8465-9cd6de5b0a5e.png)  

1. Choose neural network type on select box.
1. Tap one image to analyze from your image list.
1. Clipped image will be shown top area and execute classification.
1. The analysis results will be shown middle area and elapsed time will be shown as well.

## Neural Network
This uses trained model by ImageNet as network model of neural network.
These models are shared on following site.
ImageNet Models: https://nnabla.readthedocs.io/en/latest/python/api/models/imagenet.html  
This application is supporting three networks in it.
+ ResNet-18
+ MobileNet
+ SENet-154
+ SqueezeNet v1.1

## Requirement
- Android studio

## Install
1. Download  
   ```$ git clone https://github.com/Koichi-Hatake/DeepLearningAndroid ```  
1. Build  
   Please import by Android Studio.  
   ```File -> Open -> Open File or Project```  
1. Run  
   Run the application.  
   ```Run 'app' ```

## License
This software includes the work that is distributed in the Apache License 2.0

## Author
[Koichi-Hatake] (https://github.com/DeepLearningAndroid)
