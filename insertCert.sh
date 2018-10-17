keytool -importcert -file wwwgooglecom.crt -keystore sslproxy.jks -alias "wwwgooglecom" -storepass mko0okm
keytool -importcert -file GlobalSignRootCA-R2.crt -keystore sslproxy.jks -alias "GlobalSignRootCA-R2" -storepass mko0okm
keytool -importcert -file GoogleInternetAuthorityG3.crt -keystore sslproxy.jks -alias "GoogleInternetAuthorityG3" -storepass mko0okm

keytool -list -v -keystore sslproxy.jks -storepass mko0okm