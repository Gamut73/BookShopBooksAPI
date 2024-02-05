FROM openjdk:17.0.2-slim-buster

ARG CHROME_VERSION="119.0.6045.199-1"

#Install pre reqs
RUN apt-get update
RUN apt-get install -y wget
#RUN wget https://dl.google.com/linux/direct/google-chrome-stable_119.0.6045.200_amd64.deb
#RUN apt install -y ./google-chrome-stable_current_amd64.deb
#RUN rm google-chrome-stable_current_amd64.deb

RUN wget --no-verbose -O /tmp/chrome.deb https://dl.google.com/linux/chrome/deb/pool/main/g/google-chrome-stable/google-chrome-stable_${CHROME_VERSION}_amd64.deb \
  && apt install -y /tmp/chrome.deb \
  && rm /tmp/chrome.deb

#RUN apt-get install -y curl
#RUN apt-get install -y p7zip \
#    p7zip-full \
#    unace \
#    zip \
#    unzip \
#    bzip2

#Install chrome browser
#RUN curl http://dl.google.com/linux/chrome/deb/pool/main/g/google-chrome-stable/google-chrome-stable_$CHROME_VERSION-1_amd64.deb -o chrome.deb
#RUN dpkg -i chrome.deb
#RUN rm chrome.deb


ARG JAR_FILE=build/libs/BobShopBooksAPI-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

RUN mkdir chrome_driver
COPY chrome_driver/chromedriver.exe ./chrome_driver
RUN chmod +x ./chrome_driver/chromedriver.exe

# Spring boot entrypoint
ENTRYPOINT ["java","-jar","/app.jar"]
