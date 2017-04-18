FROM anapsix/alpine-java:jdk

RUN apk update && apk add libstdc++ curl docker wget

RUN mkdir -p /{app,opt}

RUN curl -L -o /gradle.zip http://services.gradle.org/distributions/gradle-3.5-bin.zip && \
	cd /opt && \
	unzip /gradle.zip && \
	ln -s /opt/gradle* /opt/gradle && \
	rm /gradle.zip

ENV PATH=/opt/gradle/bin:$PATH

RUN wget https://storage.googleapis.com/kubernetes-release/release/v1.6.1/bin/linux/amd64/kubectl

RUN chmod +x kubectl
RUN mv kubectl /usr/bin
ADD .kube /root/.kube/
ADD . /app/