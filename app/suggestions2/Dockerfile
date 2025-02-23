FROM python:3.13-slim-bookworm
COPY --from=ghcr.io/astral-sh/uv:latest /uv /bin/

# Install postgresql lib
RUN apt-get update && apt-get install -y --no-install-recommends libpq5

# Install app and deps (using uv)
WORKDIR /code
COPY ./requirements.txt /code/requirements.txt
RUN uv pip install --no-cache-dir --upgrade --system -r /code/requirements.txt
COPY ./app /code/app

# Start app on port 80
CMD ["fastapi", "run", "app/main.py", "--port", "80"]