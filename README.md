```  
113  sudo certbot certonly --standalone -d sentinel.demonstrator.info --email harh@zhaw.ch
  114  cd /etc/letsencrypt/live/
  115  cd /etc/letsencrypt/
  116  ls
  117  sudo su
  118  cd
  119  ls
  120  ls -al
  121  sudo chown ubuntu:ubuntu *.pem
  122  ls -al
  123  mkdir certs
  124  mv *.pem certs/
  125  ls
  126  cd certs/
  127  ls
  128  cd /etc/letsencrypt/
  129  sudo su
  130  cd
  131  ls
  132  cd certs/
  133  ls
  134  openssl pkcs12 -export -out cert.pkcs12 -in cert.pem
  135  openssl pkcs12 -export -in fullchain.pem -inkey privkey.pem -out pkcs.p12 -name sentinel
  136  ls
  137  keytool -importkeystore -deststorepass pass1234 -destkeypass pass1234 -destkeystore keystore.jks -srckeystore pkcs.p12 -srcstoretype PKCS12 -srcstorepass pass1234 -alias sentinel
  138  sudo add-apt-repository ppa:webupd8team/java
  139  sudo apt update; sudo apt install oracle-java8-installer
  140  ls
  141  keytool -importkeystore -deststorepass pass1234 -destkeypass pass1234 -destkeystore keystore.jks -srckeystore pkcs.p12 -srcstoretype PKCS12 -srcstorepass pass1234 -alias sentinel
  142  ls
```
