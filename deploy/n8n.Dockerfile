FROM n8nio/n8n:1.70.4
USER root
RUN npm install -g qrcode@1.5.4
USER node
