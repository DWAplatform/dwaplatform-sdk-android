DWAplatform Android SDK
=================================================
DWAplatform is an Android client library to work with DWAplatform.

Installation
-------------------------------------------------

### Android Studio (or Gradle)

No need to clone the repository or download any files -- just add this line to your app's `build.gradle` inside the `dependencies` section:

    compile 'com.dwaplatform:dwaplatform:1.0.0'


License
-------------------------------------------------
DWAplatform SDK is distributed under MIT license, see LICENSE file.


Contacts
-------------------------------------------------
Report bugs or suggest features using
[issue tracker at GitHub](https://github.com/DWAplatform/dwaplatform-sdk-android).


Sample usage in Kotlin
-------------------------------------------------
```kotlin
    import com.dwaplatform.android.DWAplatform;
    import com.dwaplatform.android.card.CardAPI;
    import com.dwaplatform.android.card.models.Card;


    //....

    // Configure DWAplatform
    val config = DWAplatform.Configuration("DWAPLATFORM_SANDBOX_HOSTNAME", true)
    DWAplatform.initialize(config)

    // Get card API
    val cardAPI = DWAplatform.getCardAPI(MyApplication.context)

    // Register card
    // get token from POST call: .../rest/v1/:clientId/users/:userId/accounts/:accountId/cards
    val token = "XXXXXXXXYYYYZZZZKKKKWWWWWWWWWWWWTTTTTTTFFFFFFF...."
    val cardNumber = "1234567812345678"
    val expiration = "1122"
    val cxv = "123"
    cardAPI.registerCard(token, cardNumber, expiration, cxv) { card, e ->
        if (e != null) {
            Log.e("Sample", e.message)
            return@registerCard
        }
        card?.let {
            Log.d("Sample", "card id: $card.id")
        }
    }

```
