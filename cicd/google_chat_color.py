import json
import requests
import argparse

def send_notification(webhook_url, message_body):
    # Construct the JSON payload
    card_message = {
        "cards": [
            {
                "sections": [
                    {
                        "widgets": [
                            {
                                "textParagraph": {
                                    "text": message_body
                                }
                            }
                        ]
                    }
                ]
            }
        ]
    }
    
    # Send the message
    response = requests.post(
        webhook_url, 
        data=json.dumps(card_message),
        headers={'Content-Type': 'application/json'}
    )
    
    if response.status_code != 200:
        print(f"Failed to send message: {response.status_code}, {response.text}")

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Send notification to Google Chat.')

    parser.add_argument('-m', '--message', required=True, help='Message body to send.')
    parser.add_argument("-w", "--webhook", nargs='?', type=str, dest='webhook', help='Google Chat room webhook')
    args = parser.parse_args()
    
    webhook_url = args.webhook
    message_body = args.message
    
    # Call the function to send the notification
    send_notification(webhook_url, message_body)
