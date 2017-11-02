DWAplatform Android SDK
=================================================
DWAplatform is an Android client library to work with DWAplatform.

Installation
-------------------------------------------------
SDK has been written in Kotlin.

The SDK is published as a Maven artifact on Maven Central Repository (http://search.maven.org/) and can be used with Gradle or Maven.

```
repositories {
    mavenCentral()
}

dependencies {
    compile 'com.dwaplatform:dwaplatform-sdk-android:1.0.0'
}
```

```
<dependency>
  <groupId>com.dwaplatform</groupId>
  <artifactId>dwaplatform-sdk-android</artifactId>
  <version>1.0.0</version>
</dependency>
```

License
-------------------------------------------------
MangopaySDK is distributed under MIT license, see LICENSE file.


Contacts
-------------------------------------------------
Report bugs or suggest features using
[issue tracker at GitHub](https://github.com/DWAplatform/dwaplatform-sdk-android).


Sample usage in Java
-------------------------------------------------
```
    import com.platform.android.card.DWAplatform;
	import com.platform.android.card.CardAPI;
	import com.platform.android.card.models.Card;


    //....

    // Configure DWAplatform
    DWAplatform.Configuration config = new DWAplatform.Configuration("api.sandbox.dwaplatform.com", true);
    DWAplatform.Companion.initialize(config);

    // Get card API
    final CardAPI cardAPI = DWAplatform.Companion.getCardAPI(this);

    // Register card
	// get token from POST call: .../rest/v1/:clientId/users/:userId/accounts/:accountId/cards
    final String token = "XXXXXXXX-YYYY-ZZZZ-KKKK-WWWWWWWWWWWW";
    final String cardNumber = "1234567812345678";
    final String expiration = "1122";
    final String cxv = "123";
	cardAPI.registerCard(token, cardNumber, expiration, cxv, new Function2<Card, Exception, Unit>() {
                    @Override
                    public Unit invoke(Card card, Exception e) {
                    	// now you can access to card object to request cashin, etc.
                    	println(card.getId());
                    }
				});



```