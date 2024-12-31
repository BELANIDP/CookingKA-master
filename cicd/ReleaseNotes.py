import argparse
import json
import os
import subprocess
import re
import requests
import sys


def check_latest_commit_tagged(repo_path):
    """
    This function checks if the latest commit is tagged or not in the repo_path
    :param repo_path: Repository path
    :return: bool
    """
    # print("in the check commit tagged function")
    try:
        head_tag = subprocess.run('git describe --tags HEAD'.split(), cwd=repo_path, universal_newlines=True,
                                  stdout=subprocess.PIPE, check=True).stdout.strip()
        last_tag = subprocess.run('git describe --tags --abbrev=0'.split(), cwd=repo_path, universal_newlines=True,
                                  stdout=subprocess.PIPE, check=True).stdout.strip()

        print("head_tag:", head_tag)
        print("last_tag:", last_tag)
        is_tagged = True if head_tag == last_tag else False

        if is_tagged:
            print(f"Latest commit in repository at '{repo_path}' is tagged.")
            sys.exit()
        else:
            return is_tagged

    except subprocess.CalledProcessError as e:
        print("Error occurred", e)
        sys.exit()


def get_git_commit_massages(repo_path, tag_range=None, regex=None):
    """
    This function gets set of unique_jira_ticket_ids from the commit massages between the head and last tag or
    between the two tags if input parameter-> tag range is provided.

    :param repo_path: This is a project repository path
    :param tag_range: list of two values of tags
    :param regex : A regular expression pattern to filter the commit messages.
    :return: set of unique_jira_ticket_ids
    """
    try:
        if tag_range:
            from_tag, to_tag = tag_range
        else:
            from_tag = 'Head'
            to_tag = subprocess.run('git describe --abbrev=0 --tags'.split(), cwd=repo_path, stdout=subprocess.PIPE,
                                    universal_newlines=True, check=True).stdout.strip()

        print(from_tag)
        print(to_tag)

        commit_messages = subprocess.run(f"git log --pretty=format:%s {from_tag}...{to_tag} --no-merges".split(), cwd=repo_path,
                                         stdout=subprocess.PIPE, universal_newlines=True, check=True)

        commit_messages = commit_messages.stdout

        pattern = r'\[.*?M63KA[-_]\d+.*?\]'
        ticket_pattern = r'M63KA[-_]\d+'

        all_tickets = []

        # Split the input into individual lines if it's a single string
        if isinstance(commit_messages, str):
            commit_messages = commit_messages.split('\n')

        for message in commit_messages:
            # Find all matches of the pattern in square brackets
            matches = re.findall(pattern, message)

            for match in matches:
                # Extract individual ticket IDs from each match
                tickets = re.findall(ticket_pattern, match)

                all_tickets.extend(tickets)

        unique_jira_ticket_ids = []
        for ticket in all_tickets:
            ticket = ticket.replace('_', '-')
            unique_jira_ticket_ids.append(ticket)

        unique_jira_ticket_ids = set(unique_jira_ticket_ids)
        if len(unique_jira_ticket_ids) == 0:

            sys.exit()
        print(unique_jira_ticket_ids)

        return unique_jira_ticket_ids
    except subprocess.CalledProcessError as e:
        print(f"Error occurred : {e}")


def get_jira_info(unique_jira_ticket_ids, repo_path, credentials, version=None):
    """
    This function from the unique_jira_ticket_ids gets the information of jira tickets about
    summary,issue type of respective jira ticket and stores it in a markdown file.

    :param unique_jira_ticket_ids: A set of unique jira ticket ids from git commit messages
    :param repo_path: Project repository path
    :param version: release version
    """
    print("In get_jira_info function")
    ticket_ids = unique_jira_ticket_ids

    base_url = 'https://whirlpool.atlassian.net/rest/api/2/issue/'

    auth = credentials
    jira_base_url = 'https://whirlpool.atlassian.net/browse/'

    markdown_file = os.path.join(repo_path, 'buildchangelog.md')
    issue_summaries = {}

    # Fetch details for each ticket
    for ticket_id in ticket_ids:
        url = base_url + ticket_id + '?fields=summary, issuetype'
        response = requests.get(url, auth=auth)

        if response.status_code == 200:
            print(f"Received details for {ticket_id}")
            data = response.json()

            summary = data['fields']['summary']
            issue_type = data['fields']['issuetype']['name']
            jira_ticket_url = f"{jira_base_url + ticket_id}"
            summary = f"[{ticket_id}]({jira_ticket_url}) : {summary}"
            # Append the summary to the respective issue type in the dictionary
            if issue_type in issue_summaries:
                issue_summaries[issue_type].append(summary)
            else:
                issue_summaries[issue_type] = [summary]
        else:
            print(f"Failed to retrieve details for ticket {ticket_id}. Status code: {response.status_code}")

    project_key = 'M63KA'
    # Write issues to Markdown file
    with open(markdown_file, 'w') as file:

        file.write("------------------------------------------------\n")
        file.write(f"# Release notes - {project_key} - {version}\n\n")
        # Iterate through each issue type and its associated summaries
        for issue_type, summaries in issue_summaries.items():
            # Write the header for the issue type
            file.write(f"### {issue_type}\n")

            # Write each summary under the respective header
            for summary in summaries:
                file.write(f" {summary}\n")

            # Add an empty line between each issue type
            file.write('\n')

    print(f"Ticket details written to {markdown_file}")


def check_tags_format_correct(tag_range):
    tag1, tag2 = tag_range

    # Define patterns for the two formats
    pattern1 = r'^v\d+\.\d+\.\d+$'  # For format: v0.0.1
    pattern2 = r'^v\d+\.\d+\.\d+-\d{12}$'  # For format: v0.5.7-202410012235

    # Check if both tags match either pattern1 or pattern2
    def is_valid_tag(tag):
        return re.match(pattern1, tag) or re.match(pattern2, tag)

    # Check if both tags are valid
    if is_valid_tag(tag1) and is_valid_tag(tag2):
        return True
    else:
        return False


def check_tags_in_current_branch(tag_range, repo_path):
    """
    This function checks if the two tags provided in the tag range are present in the current branch if not
    returns false

    :param tag_range:
    :param repo_path:
    :return: true or false
    """
    try:
        tag1, tag2 = tag_range
        # Get the current branch name
        current_branch = subprocess.check_output("git rev-parse --abbrev-ref HEAD".split(), cwd=repo_path,
                                                 universal_newlines=True).strip()

        tags_in_branch = subprocess.check_output(['git', 'tag'], cwd=repo_path, universal_newlines=True).split()

        # Check if both tags are in the current branch
        tags_present = True
        missing_tags = []

        if tag1 not in tags_in_branch:
            missing_tags.append(tag1)
            tags_present = False
        if tag2 not in tags_in_branch:
            missing_tags.append(tag2)
            tags_present = False

        if tags_present:
            print(f"Both tags {tag1} and {tag2} are present in the current {current_branch} branch.")
        else:
            missing_tags_str = ", ".join(missing_tags)
            print(f"The following tags are not present in the current {current_branch} branch: {missing_tags_str}")

        return tags_present
    except subprocess.CalledProcessError as e:
        print(f"An error occurred while checking tags: {e}")
        return False


def check_release_version(version_name, credentials):
    #project_key = 'EESINFRA'
    project_key = 'M63KA'
    url = f"https://whirlpool.atlassian.net/rest/api/2/project/{project_key}/versions"

    response = requests.get(url, auth=credentials)

    if response.status_code != 200:
        raise Exception(f"Failed to retrieve versions: {response.status_code} - {response.text}")

    versions = response.json()
    return any(version['name'] == version_name for version in versions)


def create_release(credentials, version_name):
    """Creates a release in Jira with version_name."""

    url = "https://whirlpool.atlassian.net/rest/api/3/version"
    #project_key = 'EESINFRA'
    project_key = 'M63KA'  # project key for android jira board
    description = version_name + ' release'
    headers = {
        "Accept": "application/json",
        "Content-Type": "application/json"
    }

    payload = {
        "name": version_name,
        "project": project_key,
        "description": description,
        "released": False
    }

    try:
        response = requests.post(url, data=json.dumps(payload), headers=headers, auth=credentials)
        response.raise_for_status()  # Raises an exception if the response status code is not 200 (success)
        return response.json()
    except requests.exceptions.HTTPError as err:
        # Handle HTTP errors here
        print("HTTP Error:", err)
        error_response = response.json()  # Parse JSON response
        errors = error_response.get('errors')
        if errors:
            for field, message in errors.items():
                print(f"Error in field '{field}': {message}")
    except requests.exceptions.RequestException as e:
        # Catch any other exceptions
        print("Request Exception:", e)


def update_ticket(unique_jira_ticket_ids, credentials, version_name):
    """
    This function updates the jira ticket ids inside set unique_jira_ticket_ids with the 
    version_name provided in input paramter
    """
    # Base URL of Jira instance
    #unique_jira_ticket_ids = {'EESINFRA-919'} 
    for key in unique_jira_ticket_ids:

        issue_key = key
        url = "https://whirlpool.atlassian.net/rest/api/3/issue/" + issue_key

        # Prepare the payload
        payload = {
            "fields": {
                "fixVersions": [
                    {
                        "name": version_name
                    }
                ]
            }
        }

        # Headers
        headers = {
            "Accept": "application/json",
            "Content-Type": "application/json"
        }

        try:
            # Authenticate and send the request
            response = requests.put(url, auth=credentials, headers=headers, data=json.dumps(payload))

            # Check response
            if response.ok:
                print(f"Release version updated successfully for issue {issue_key}")
            else:
                error_message = response.text if response.text else "No error message provided."
                print(
                    f"Failed to update release version for issue {issue_key}. Status code: {response.status_code}, Error: {error_message}")
        except requests.exceptions.RequestException as e:
            # Handle any request exception
            print(f"Request Exception occurred: {e}")


def clone_or_update_repo(repo_link, repo_path):
    """Clone or update the repository based on the provided link."""
    if not os.path.exists(repo_path):
        try:
            subprocess.run(f"git clone {repo_link}".split(), check=True)
        except subprocess.CalledProcessError as e:
            print(f"Error cloning repository: {e.stderr}")
            sys.exit(1)
    else:
        try:
            subprocess.run('git pull'.split(), cwd=repo_path, check=True)
        except subprocess.CalledProcessError as e:
            print(f"Error updating repository: {e.stderr}")
            sys.exit(1)
            
def checkout_branch(repo_path, branch):
    """Checkout the specified branch."""
    subprocess.run(["git", "-C", repo_path, "checkout", branch], check=True)

def get_repo_path(args):
    """Determine the repository path based on input arguments."""
    if args.repo_link:
        repo_path = os.path.join(os.getcwd(), os.path.basename(args.repo_link).replace(".git", ""))
        clone_or_update_repo(args.repo_link, repo_path)
    elif args.repo_path:
        repo_path = args.repo_path
    else:
        repo_path = os.getcwd()

    print(f"Repository Path: {repo_path}")
    return repo_path


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('-r', "--repo_link", nargs='?', help="Provide the repository link of github")
    parser.add_argument('-p', "--repo_path", nargs='?', help="provide the repository path on local")
    parser.add_argument('-t', "--tag_range", nargs='*', help="provide the {first tag} space {second tag} from which commits messages needs to capture")
    parser.add_argument('-v', "--version", nargs='?', help="If provided creates a Release version in Jira with status UNRELEASED and assignes the jira tickets with this release version ")
    parser.add_argument('-b', "--branch", nargs='?', help="provide the Branch to checkout")
    parser.add_argument('-usr', "--User_name", nargs='?', help="Provide whirlpool email id as username")
    parser.add_argument('-token', "--Jira_Api_Token", nargs='?', help="Provide the jira api token")
    args = parser.parse_args()

    version = args.version

    if args.User_name:
        email = args.User_name
    else:
        email = os.getenv('User_name')

    if args.Jira_Api_Token:
        api_token = args.Jira_Api_Token
    else:
        api_token = os.getenv('JIRA_API_TOKEN')

    credentials = (email, api_token)
    print(email)

    # get the repository path based on input parameters
    repo_path = get_repo_path(args)

    if args.branch:
        checkout_branch(repo_path, args.branch)
        print(f"Checked out to branch: {args.branch}")

    if args.tag_range:
        # If tag_range is given create release notes for the given tag range
        if check_tags_format_correct(args.tag_range):
            try:
                if check_tags_in_current_branch(args.tag_range, repo_path):

                    unique_jira_ticket_ids = get_git_commit_massages(repo_path, args.tag_range)
                    # create release notes in a markdown file
                    get_jira_info(unique_jira_ticket_ids, repo_path, credentials, version)
                else:
                    print("Exit"
                          "ing script.")
                    sys.exit(1)
            except subprocess.CalledProcessError as e:
                print(f"Error occurred : {e}")

        else:
            print("Incorrect tags format was given")
            sys.exit(1)

    elif not check_latest_commit_tagged(repo_path):
        # If tag_range is not given then create release notes for current release tag and previous tag on repository
        try:
            print(f"Latest commit in repository at '{repo_path}' is not tagged.")
            # Get unique jira ticket ids from git commit messages
            unique_jira_ticket_ids = get_git_commit_massages(repo_path)

            # create release notes in a markdown file
            get_jira_info(unique_jira_ticket_ids, repo_path, credentials, version)

        except subprocess.CalledProcessError as e:
            print(f"Error occurred : {e}")

# Create a release if args.version input parameter is given

    # check if release version present in jira
#     if args.version:
#         version_exists = check_release_version(version, credentials)
#         if version_exists:
#             print(f"Release version exists in jira. Updating the jira tickets with {version} version..")
#             update_ticket(unique_jira_ticket_ids,  credentials, version_name=version)
#         else:
#             # If release version not exists create release in jira with release version
#             release_info = create_release(credentials, version_name=version)
#             # Check the response
#             if release_info:
#                 print("Release created successfully! Updating the jira tickets with release version..")
#                 print(json.dumps(release_info, sort_keys=True, indent=4, separators=(",", ": ")))
#                 update_ticket(unique_jira_ticket_ids, credentials, version_name=version)
#             else:
#                 print("Failed to create release.")


if __name__ == "__main__":
    main()
