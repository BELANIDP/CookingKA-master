import sys
import argparse
import pprint
from json import dumps
from httplib2 import Http


def parse_inputs(argv):
    """
    This function is to parse the input argments
    :param argv: command line arguments
    :return: processed args
    """
    pprint.pprint(argv)
    parser = argparse.ArgumentParser(description="This script sends messages to google chat rooms \
                                                 based on the configured google chat rooms webhook"
                                     )
    parser.add_argument("-m", "--message", nargs='?', type=str, dest='message',
                        help='Message to be sent to google room', required=True)
    parser.add_argument("-w", "--webhook", nargs='?', type=str, dest='webhook', help='Google Chat room webhook')

    args = parser.parse_args()
    return args


def send_message(message, webhook_url=None):
    """
    Hangouts Chat incoming webhook quickstart.
    This functions sends  given message to given google chat webhook
    Inputs:
    message: This is the message that needs to be sent
    webhook_url: This is the webhook url to send message
    :returns: None
    """
    
    if webhook_url is None:
        # Below webhook url is for AOSP_Test_Results google chat room
        url = 'https://chat.googleapis.com/v1/spaces/AAAAoo_W8M8/messages?key=AIzaSyDdI0hCZtE6vySjMm-WEfRq3CPzqKqqsHI&token=vIwSsp-35bQbPfBG51p-U3rhHgH44ikx-XIQceogQhk%3D'
    else:
        url = webhook_url

    new_message = message.replace('\\n', '\n')
    bot_message = {
        # 'text' : f'Hello from a *Srini* Python script!\n This message mentions all users <users/all> and <users/107389433162865916722>' }
        # 'text': f"{new_message}"}
        'text': "{}".format(new_message)}

    message_headers = {'Content-Type': 'application/json; charset=UTF-8'}

    http_obj = Http()
    
    try: 
        response = http_obj.request(
                uri=url,
                method='POST',
                headers=message_headers,
                body=dumps(bot_message),
                )
    except Exception as e:
        # print(f"There is an exception: {e}")
        print("There is an exception: {}".format(e))

    print(response)


def main(input_args):
    """
    This is main method for processing command line arguments and also to send message to google chat roon
    :param input_args: Command line arguments
    :return: None
    """
    cmd_args = parse_inputs(input_args)
    print(cmd_args.message, cmd_args.webhook)
    send_message(cmd_args.message, cmd_args.webhook)


if __name__ == '__main__':
    main(sys.argv[1:])
