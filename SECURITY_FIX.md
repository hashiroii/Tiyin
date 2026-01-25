# Security Fix: API Key Leak

## What Happened
The `google-services.json` file containing Firebase API keys was committed to the repository in commit `b65aca9`.

## Exposed Keys
- Firebase API Key: `AIzaSyDmgKW-h0grya9xPl80odwyY7PgWTlGPsE`
- OAuth Client IDs (these are less critical but should be rotated)

## Immediate Actions Required

### 1. Rotate Firebase API Keys
**Firebase API keys are managed in Google Cloud Console, not Firebase Console:**

1. Go to [Google Cloud Console - API Credentials](https://console.cloud.google.com/apis/credentials?project=tiyin-b7c62)
2. Find the API key: `AIzaSyDmgKW-h0grya9xPl80odwyY7PgWTlGPsE`
3. **Option A - Restrict the key (Recommended first step):**
   - Click on the key
   - Add API restrictions (limit to Firebase APIs only)
   - Add Application restrictions (Android app package: `kz.hashiroii.tiyin`)
   - Save
4. **Option B - Delete and regenerate:**
   - Click "Delete" to remove the exposed key
   - Go to Firebase Console → Project Settings → General
   - Download a new `google-services.json` (this will have a new API key)
   - Or manually create a new API key in Google Cloud Console
5. Update your local `google-services.json` with the new key

### 2. Clean Git History (Choose one method)

#### Option A: Using git-filter-repo (Recommended)
```bash
# Install git-filter-repo first: pip install git-filter-repo
git filter-repo --path app/google-services.json --invert-paths --force
git push origin --force --all
```

#### Option B: Using BFG Repo-Cleaner
```bash
# Download BFG from https://rtyley.github.io/bfg-repo-cleaner/
java -jar bfg.jar --delete-files google-services.json
git reflog expire --expire=now --all && git gc --prune=now --aggressive
git push origin --force --all
```

#### Option C: Manual filter-branch (if above not available)
```bash
git filter-branch --force --index-filter \
  'git rm --cached --ignore-unmatch app/google-services.json' \
  --prune-empty --tag-name-filter cat -- --all
git push origin --force --all
```

### 3. Notify Team Members
All team members need to:
1. Delete their local repository
2. Clone fresh from the cleaned remote
3. Add their own `google-services.json` file locally

### 4. Verify
After cleaning history, verify the file is gone:
```bash
git log --all --full-history -- app/google-services.json
# Should return nothing
```

## Prevention
- ✅ `google-services.json` is now in `.gitignore`
- ✅ `google-services.json.example` template created
- ⚠️ Always check `.gitignore` before committing sensitive files
- ⚠️ Use environment variables or local.properties for API keys
