# Video Calling App

### A video calling application built using Huawei Cloud DB and WebRTC.
<a href="https://medium.com/huawei-developers-tr/webrtc-nedir-ve-kotlinde-huawei-clouddb-ile-nas%C4%B1l-kullan%C4%B1l%C4%B1r-755feb42f3b1">Medium Article Link</a>

![Header Image](https://user-images.githubusercontent.com/29903779/172182102-69970b84-9888-41dd-98d8-9bff6fdc1c34.png)

## Project Features
 - MVVM (Model, View, ViewModel)
 - LiveData
 - Navigation Component (NavGraph)
 - Hilt for Dependency Injection
 - WebRTC to perform Video and Voice call (<a href="https://webrtc.org/">Source</a>)
 - Signing in with Huawei ID using Huawei Auth Service (<a href="https://developer.huawei.com/consumer/en/doc/development/AppGallery-connect-Guides/agc-auth-introduction-0000001053732605">Source</a>)
 - Cloud DB to store data (<a href="https://developer.huawei.com/consumer/en/doc/development/AppGallery-connect-Guides/agc-clouddb-introduction-0000001054212760">Source</a>)

## Login Screen
### With the Sign in with Huawei ID button, you can log in to the application using Huawei Auth Service.
<p align="left">
<img src="https://user-images.githubusercontent.com/29903779/172160808-06793cd6-b795-45f1-b915-eba847a3ec39.jpg" width="250" height="530"/>
</p>

## Main Screen
### You can specify an id for the conversation or create a random id with the random button and start the conversation with the Create button.
### The other person can join the call with the Join button using the ID from which the call was initiated.
<p align="left">
<img src="https://user-images.githubusercontent.com/29903779/172160813-48a74e80-5bd6-4c31-b2e1-d9867670db5e.jpg" width="250" height="530"/>
</p>

## Call Screen
### The interface of the conversation screen was designed in a classical way. You can use the switch camera, microphone on/off, camera on/off, end call buttons.
<p align="left">
<img src="https://user-images.githubusercontent.com/29903779/172161048-f5b74691-2757-470e-89ff-f0d1dc3b89c7.jpg" width="250" height="530"/>
</p>
