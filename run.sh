#!/bin/bash
# run.sh - Run the AI Device Login Security System

if [ ! -d "out" ]; then
  echo "Project not compiled yet. Running compile.sh first..."
  bash compile.sh
fi

echo ""
echo "Starting AI-Based Device Login Security System..."
echo ""

# Optional: export your OpenAI API key here
# export OPENAI_API_KEY=sk-your-key-here

java -cp out com.security.Main
