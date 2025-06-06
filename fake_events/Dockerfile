# Use an official image
FROM apache/spark:3.5.0

USER root

# Set the working directory inside the container
WORKDIR /app

# Copy script & requirements 
COPY requirements.txt .
COPY fake_data.py .

# Install dependencies
RUN apt-get update && apt-get install -y python3-pip && \
    pip3 install --no-cache-dir -r requirements.txt

# Change ownership of /app to the spark user
RUN chown -R spark:spark /app

# Switch back to the spark user
USER spark

