#!/bin/bash

# Test script for Devin API session creation
# Usage: ./test-devin-api.sh YOUR_DEVIN_API_KEY

if [ -z "$1" ]; then
  echo "❌ Error: DEVIN_API_KEY is required"
  echo "Usage: ./test-devin-api.sh YOUR_DEVIN_API_KEY"
  exit 1
fi

DEVIN_API_KEY="$1"

echo "🚀 Testing Devin API session creation..."
echo ""

# Create Devin session
DEVIN_SESSION=$(curl -X POST "https://api.devin.ai/v1/sessions" \
  -H "Authorization: Bearer $DEVIN_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test SonarQube Vulnerability Remediation",
    "repo_url": "https://github.com/mbatchelor81/inventory-demo",
    "branch": "main",
    "prompt": "Review the SonarQube scan results for project mbatchelor81_inventory-demo at https://sonarcloud.io/dashboard?id=mbatchelor81_inventory-demo. There are 5 vulnerabilities detected (test).\n\n**IMPORTANT**: You have access to the SonarQube MCP server. Use it to:\n- Query detailed vulnerability information directly from SonarQube\n- Get specific code locations and severity levels\n- Retrieve remediation recommendations from SonarQube\n- Access the full issue context and history\n\nPlease:\n1. Use the SonarQube MCP server to analyze each vulnerability in detail\n2. Review severity levels, affected files, and specific code lines\n3. Fix security vulnerabilities following OWASP best practices\n4. Update dependencies if needed\n5. Ensure all tests pass after fixes\n6. Create a pull request titled: fix: [TEST] Remediate SonarQube vulnerabilities\n\nPR body should include:\n- Number of vulnerabilities addressed: 5\n- List of specific vulnerabilities fixed (with SonarQube issue keys)\n- SonarQube Report: https://sonarcloud.io/dashboard?id=mbatchelor81_inventory-demo\n\nFocus on:\n- SQL injection vulnerabilities\n- XSS vulnerabilities\n- Authentication/authorization issues\n- Insecure dependencies\n- Code quality issues that could lead to security problems"
  }')

echo "📥 Response:"
echo "$DEVIN_SESSION" | jq '.'
echo ""

# Extract session details
SESSION_ID=$(echo "$DEVIN_SESSION" | jq -r '.session_id // .id // "N/A"')
SESSION_URL=$(echo "$DEVIN_SESSION" | jq -r '.url // "N/A"')
ERROR_MESSAGE=$(echo "$DEVIN_SESSION" | jq -r '.error // .detail // "N/A"')

if [ "$SESSION_ID" != "N/A" ] && [ "$SESSION_ID" != "null" ]; then
  echo "✅ Success!"
  echo "   Session ID: $SESSION_ID"
  echo "   Session URL: $SESSION_URL"
  echo ""
  echo "🔗 View your Devin session: $SESSION_URL"
else
  echo "❌ Failed to create session"
  if [ "$ERROR_MESSAGE" != "N/A" ]; then
    echo "   Error: $ERROR_MESSAGE"
  fi
fi
