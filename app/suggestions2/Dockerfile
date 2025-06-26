FROM python:3.13-slim-bookworm
COPY --from=ghcr.io/astral-sh/uv:latest /uv /bin/

ENV SUGGESTIONS2_ENDPOINT_PORT=80

# Install postgresql lib
RUN apt-get update && apt-get install -y --no-install-recommends libpq5

# Install app and deps (using uv)
WORKDIR /code
COPY ./pyproject.toml /code/
COPY ./app /code/app
RUN uv sync --no-cache

# Start app
# "sh -c" is used to allow expansion of environment variable.
CMD ["sh", "-c", "uv run fastapi run app/setup_fastapi.py --host 0.0.0.0 --port ${SUGGESTIONS2_ENDPOINT_PORT}"]