# Twilio SMS Integration Setup Guide

## Overview
Your banking system now supports real SMS delivery via Twilio for OTP authentication. The system works in two modes:
- **Demo Mode**: OTP displayed in console (when Twilio is disabled)
- **SMS Mode**: OTP sent via SMS to actual phone numbers (when Twilio is enabled)

## Step 1: Create Twilio Account
1. Go to [https://www.twilio.com/](https://www.twilio.com/)
2. Sign up for a free account
3. Verify your phone number during signup

## Step 2: Get Twilio Credentials
After creating your account:

1. **Account SID**: Found on your Twilio Console Dashboard
2. **Auth Token**: Found on your Twilio Console Dashboard (click "Show" to reveal)
3. **Phone Number**: Purchase a phone number or use the trial number

### Trial Account Limitations
- Can only send SMS to verified phone numbers
- Limited to 500 SMS messages
- SMS will include "Sent from your Twilio trial account" message

## Step 3: Configure Application
Update your `application.properties` file:

```properties
# Twilio Configuration
twilio.account.sid=YOUR_ACTUAL_ACCOUNT_SID
twilio.auth.token=YOUR_ACTUAL_AUTH_TOKEN
twilio.phone.number=YOUR_TWILIO_PHONE_NUMBER
twilio.enabled=true
```

### Example Configuration:
```properties
# Twilio Configuration
twilio.account.sid=AC1234567890abcdef1234567890abcdef
twilio.auth.token=your_auth_token_here
twilio.phone.number=+1234567890
twilio.enabled=true
```

## Step 4: Phone Number Format
The system automatically formats phone numbers:
- **Indian numbers**: Adds +91 prefix (e.g., 9876543210 → +919876543210)
- **International**: Supports any country code with + prefix
- **Verification**: For trial accounts, verify recipient numbers in Twilio Console

## Step 5: Testing

### Demo Mode (Twilio Disabled)
```properties
twilio.enabled=false
```
- OTP appears in browser console and server logs
- No SMS charges
- Good for development/testing

### SMS Mode (Twilio Enabled)
```properties
twilio.enabled=true
```
- Real SMS sent to phone numbers
- Uses Twilio credits
- Production-ready

## Step 6: Verify Setup
1. Restart your application after updating configuration
2. Try logging in with a phone number
3. Check logs for success/error messages:
   - Success: "OTP sent via SMS to phone number: +919876543210"
   - Error: "Failed to send OTP via SMS to phone number: +919876543210"

## Security Features
- **Rate Limiting**: Max 5 OTPs per hour per phone number
- **Expiration**: OTPs expire in 5 minutes
- **Attempt Limiting**: Max 3 verification attempts per OTP
- **Auto Cleanup**: Expired OTPs automatically removed

## Troubleshooting

### Common Issues:
1. **Invalid credentials**: Check Account SID and Auth Token
2. **Phone number format**: Ensure proper E.164 format (+country_code_number)
3. **Trial limitations**: Verify recipient numbers in Twilio Console
4. **Network issues**: Check internet connectivity

### Log Messages:
- `Twilio initialized successfully` - Configuration is correct
- `Failed to initialize Twilio` - Check credentials
- `Twilio is disabled` - SMS service is turned off
- `SMS sent successfully` - Message delivered

## Cost Considerations
- **Trial Account**: Free with limitations
- **Pay-as-you-go**: ~$0.0075 per SMS (varies by country)
- **Monthly Plans**: Available for high-volume usage

## Production Recommendations
1. Use environment variables for sensitive credentials
2. Enable Twilio webhook for delivery status
3. Implement SMS delivery failure handling
4. Monitor usage and costs
5. Consider SMS templates for compliance

## Support
- Twilio Documentation: [https://www.twilio.com/docs](https://www.twilio.com/docs)
- Twilio Console: [https://console.twilio.com/](https://console.twilio.com/)
- Phone Number Verification: Console → Phone Numbers → Verified Caller IDs
