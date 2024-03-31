import json
import aiohttp
import asyncio
from datetime import datetime
from openai import OpenAI


max_length = 500


async def get_summary(
    session: aiohttp.ClientSession,
    text,
):
    async with session.post(
        url="https://api.openai.com/v1/chat/completions",
        headers={"Authorization": f"Bearer "},
        json={
            "model": "gpt-3.5-turbo",
            "messages": [{"role": "user", "content": f"{text}"}],
            "temperature": 0.7,
        },
    ) as response:
        s = await response.text()
        return json.loads(s)


async def process_all(instructions):
    async with aiohttp.ClientSession(
        connector=aiohttp.TCPConnector(ssl=False)
    ) as session:
        responses = {}
        tasks = {}
        async with asyncio.TaskGroup() as tg:
            for key, instruction in instructions.items():
                task = tg.create_task(get_summary(session, instruction))
                tasks[key] = task
        for key, task in tasks.items():
            responses[key] = task.result()
        return responses


def get_summary_from_dict(obj):
    if "choices" in obj:
        if obj["choices"]:
            return obj["choices"][0]["message"]["content"]
    return None


def process(key, presentation, summary, summaries, api_key):
    client = OpenAI(api_key=api_key)
    conversation_history = [
        {
            "role": "system",
            "content": "Tu es un spécialiste de l'orientation des lycéens qui m'assiste pour résumer des descriptifs de formation de l'enseignement supérieur.",
        }
    ]

    if summary:
        print("Using previous summary as presentation")
        presentation = summary

    length = len(presentation)
    compressMore = 0
    while length > max_length:
        print(f"Processing {key}...")
        if len(conversation_history) > 1:
            instruction = f"Résume encore davantage."
        else:
            instruction = f"Résume ce descriptif d'une formation de l'enseignement supérieur. '{presentation}'"

        chunk = {"role": "user", "content": instruction}
        conversation_history.append(chunk)

        print(conversation_history)

        response = client.chat.completions.create(
            model="gpt-3.5-turbo",  # "gpt-4",
            messages=conversation_history,
            stream=False,
        )

        summary = None
        if response.choices and response.choices[0].message.content is not None:
            summary = response.choices[0].message.content
        else:
            print(response)
            break

        print(f"Processed {key}")

        chunk = {"role": "assistant", "content": summary}
        conversation_history.append(chunk)

        summaries[key] = summary
        with open("../data/onisep_scrap/summaries.json", "w+") as g:
            json.dump(summaries, g, indent=2)
        length = len(summary)
        print("--------------------------------------------------")
        print(presentation)
        print("")
        print("")
        print(summary)
        print("")
        if length > max_length:
            print(f"One more round since length is {length}")
            compressMore = compressMore + 1
        else:
            break


async def main():
    summaries = {}

    with open("./config.json") as f:
        api_key = json.load(f)["api_key"]

    with open("../data/onisep_scrap/summaries.json") as f:
        summaries = json.load(f)
        now = datetime.now().strftime("%Y-%m-%d %H-%M-%S")  # current date and time
        with open(f"../data/onisep_scrap/summaries{now}.json", "w+") as g:
            json.dump(summaries, g, indent=2)

    with open("../data/onisep_scrap/descriptifs.json") as f:
        descriptifs = json.load(f)["keyToDescriptifs"]
        for key, value in descriptifs.items():
            summary = None
            if key in summaries:
                summary = summaries[key]
            if summary and len(summary) <= max_length:
                print(f"Skipping {key} already ok")
                continue
            if "presentation" in value:
                presentation = value["presentation"]
                process(key, presentation, summary, summaries, api_key)


if __name__ == "__main__":
    asyncio.run(main())
