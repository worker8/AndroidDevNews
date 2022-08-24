### Description
This application brings you the latest Android Developer news from the community/official channels by aggregating a few sources such as [/r/AndroidDev subreddit](https://reddit.com/r/androiddev), popular podcasts (e.g. [Fragmented Podcast](https://fragmentedpodcast.com/), [Android Developer Backstage](https://androidbackstage.blogspot.com/), etc), [Android Weekly](https://androidweekly.net/), [Kotlin Weekly](http://www.kotlinweekly.net/). 

I intend to add more sources, such as [ProAndroidDev](https://proandroiddev.com/), [Android Developer at Medium](https://medium.com/androiddevelopers), [Android Developer Blog](https://android-developers.googleblog.com) and others. However this will come later, because the current features are still not yet polished, for example there is no way to share a post, there are various minor bugs surrounding the podcast listening feature among others.

I created this project as a learning process for the Jetpack Compose library, so I only develop it while I can find a tad bit of free time. This project is not yet released to the PlayStore since it's still pretty rough around the edges. It is however pretty usable, so give it a go by building the project if you're interested!

Drop an issue if you have a problem, suggestion or anything!


## Screenshots

Here are some screenshots of the app.

### r/AndroidDev Subreddit
| Main | Comments |
| - | - |
| <img src="https://user-images.githubusercontent.com/1988156/151374120-b0b51876-a985-44dd-9503-bb8d40343b25.png" width="250px" /> | <img src="https://user-images.githubusercontent.com/1988156/151373905-980fcede-cc63-45f5-9b40-6cb266abace2.png" width="250px" /> |


### Podcast
<img src="https://user-images.githubusercontent.com/1988156/151373909-584e4222-382c-4194-87bf-62b440a2d3d0.png" width="250px" />

- Now in Android
- Fragmented Podcast
- Android Developer Backstage

### Newsletter
| Android Weekly | Kotlin Weekly |
| - | - |
| <img src="https://user-images.githubusercontent.com/1988156/151373911-32c65b08-faf3-40e7-8f03-a4e466a654f5.png" width="250px" /> | <img src="https://user-images.githubusercontent.com/1988156/151373916-52c67f9b-faa6-4db1-8cad-95a1a53eda35.png" width="250px" /> |

### Technology Stack
#### Compose
This application is written fully with Jetpack Compose, with some compatibility AndroidView, e.g. WebView.

#### To be filled in
👷‍♀️ 🏗️ 👷‍♂️

### Development
Prepare `secrets.properties` at the root of the project with reddit's client id, the content of the file should look like this:

```
reddit_client_id=abcdG9Lt4-ABCg
```

### About this project
This is a personal project that I build during my very limited free time to learn and test out some things that I couldn't test at work.
Feel free to open an issue if you would like to see a new feature, or interested to contribute a new feature!
