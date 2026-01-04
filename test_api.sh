#!/bin/bash

# Configuration
# Configuration
BASE_URL="http://localhost:8080/api"
RAND=$(openssl rand -hex 2) # 4 chars
NASABAH_USER="cust_${RAND}"
MARKETING_USER="mkt_${RAND}"
BRANCH_USER="bm_${RAND}"
BO_USER="bo_${RAND}"
PASS="password123"

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m'

function print_step() {
    echo -e "${GREEN}=== $1 ===${NC}"
}

function check_error() {
    if [ $? -ne 0 ]; then
        echo -e "${RED}Failed: $1${NC}"
        exit 1
    fi
}

# 1. Register Users
print_step "Registering Users"

# Nasabah
echo "Registering Nasabah..."
curl -s -X POST "$BASE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d "{
    \"username\": \"$NASABAH_USER\",
    \"email\": \"$NASABAH_USER@example.com\",
    \"password\": \"$PASS\",
    \"role\": [\"nasabah\"],
    \"fullName\": \"Nasabah Test\",
    \"phoneNumber\": \"08123456789\",
    \"address\": \"Test Address\"
  }" | jq .
check_error "Register Nasabah"

# Marketing
echo "Registering Marketing..."
curl -s -X POST "$BASE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d "{
    \"username\": \"$MARKETING_USER\",
    \"email\": \"$MARKETING_USER@example.com\",
    \"password\": \"$PASS\",
    \"role\": [\"marketing\"],
    \"fullName\": \"Marketing Test\",
    \"phoneNumber\": \"08129999999\",
    \"address\": \"Office\"
  }" | jq .
check_error "Register Marketing"

# Branch Manager
echo "Registering Branch Manager..."
curl -s -X POST "$BASE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d "{
    \"username\": \"$BRANCH_USER\",
    \"email\": \"$BRANCH_USER@example.com\",
    \"password\": \"$PASS\",
    \"role\": [\"branch_manager\"],
    \"fullName\": \"Branch Test\",
    \"phoneNumber\": \"08128888888\",
    \"address\": \"Office\"
  }" | jq .
check_error "Register Branch Manager"

# Back Office
echo "Registering Back Office..."
curl -s -X POST "$BASE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d "{
    \"username\": \"$BO_USER\",
    \"email\": \"$BO_USER@example.com\",
    \"password\": \"$PASS\",
    \"role\": [\"back_office\"],
    \"fullName\": \"BO Test\",
    \"phoneNumber\": \"08127777777\",
    \"address\": \"Office\"
  }" | jq .
check_error "Register Back Office"


# 2. Login and Get Tokens
print_step "Logging In"

login() {
    local user=$1
    local role=$2
    echo "Logging in as $role ($user)..." >&2
    local response=$(curl -s -X POST "$BASE_URL/auth/login" \
      -H "Content-Type: application/json" \
      -d "{
        \"username\": \"$user\",
        \"password\": \"$PASS\"
      }")
    echo $response >&2 # Debug
    # Check if data.token exists (legacy/expected) or root token (current reality)
    TOKEN=$(echo $response | jq -r '.data.token // .token')
    echo $TOKEN
}

NASABAH_TOKEN=$(login "$NASABAH_USER" "Nasabah")
MARKETING_TOKEN=$(login "$MARKETING_USER" "Marketing")
BRANCH_TOKEN=$(login "$BRANCH_USER" "Branch Manager")
BO_TOKEN=$(login "$BO_USER" "Back Office")

if [ "$NASABAH_TOKEN" == "null" ] || [ -z "$NASABAH_TOKEN" ]; then
    echo -e "${RED}Login failed for Nasabah${NC}"
    exit 1
fi

# 3. Check Plafonds
print_step "Checking Plafonds"
curl -s -X GET "$BASE_URL/public/plafonds" | jq .
check_error "Get Plafonds"

# 4. Apply Loan
print_step "Applying for Loan (Nasabah)"
LOAN_RES=$(curl -s -X POST "$BASE_URL/loans/apply" \
  -H "Authorization: Bearer $NASABAH_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"plafondId\": 1,
    \"amount\": 10000000,
    \"tenor\": 12
  }")

echo $LOAN_RES | jq .
LOAN_ID=$(echo $LOAN_RES | jq -r '.id')
echo "Created Loan ID: $LOAN_ID"

if [ "$LOAN_ID" == "null" ]; then
    echo -e "${RED}Loan Application Failed${NC}"
    exit 1
fi

# 5. Marketing Review
print_step "Marketing Review"
# Get list first
echo "Marketing Loans List:"
curl -s -X GET "$BASE_URL/loans/review" \
  -H "Authorization: Bearer $MARKETING_TOKEN" | jq .

echo "Approving Loan ID $LOAN_ID..."
curl -s -X POST "$BASE_URL/loans/$LOAN_ID/review?approve=true" \
  -H "Authorization: Bearer $MARKETING_TOKEN" | jq .
check_error "Marketing Review"


# 6. Branch Manager Approval
print_step "Branch Manager Approval"
echo "Branch Manager Loans List:"
curl -s -X GET "$BASE_URL/loans/approval" \
  -H "Authorization: Bearer $BRANCH_TOKEN" | jq .

echo "Approving Loan ID $LOAN_ID..."
curl -s -X POST "$BASE_URL/loans/$LOAN_ID/approve?approve=true" \
  -H "Authorization: Bearer $BRANCH_TOKEN" | jq .
check_error "Branch Approval"


# 7. Back Office Disbursement
print_step "Back Office Disbursement"
echo "Disbursement List:"
curl -s -X GET "$BASE_URL/loans/disbursement" \
  -H "Authorization: Bearer $BO_TOKEN" | jq .

echo "Disbursing Loan ID $LOAN_ID..."
curl -s -X POST "$BASE_URL/loans/$LOAN_ID/disburse?method=TRANSFER" \
  -H "Authorization: Bearer $BO_TOKEN" | jq .
check_error "Disbursement"

# 8. Check Notifications
print_step "Checking Notifications (Nasabah)"
curl -s -X GET "$BASE_URL/notifications" \
  -H "Authorization: Bearer $NASABAH_TOKEN" | jq .

print_step "Test Completed Successfully"
