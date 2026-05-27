import subprocess

hashed_password = '$2a$10$vBccO75ogP3nxoWdyNxlbuG03t5eEobUeGViR2eFgipELeBXDmdZq'

cmd = ['mysql', '-u', 'root', '-p123456', '-D', 'suhualin', '-e', "INSERT INTO db_account (username, password, email, role, register_time, del_flag) VALUES ('testuser', '" + hashed_password + "', 'test@example.com', 'user', NOW(), '0')"]

result = subprocess.run(cmd, capture_output=True, text=True)
print("STDOUT:", result.stdout)
print("STDERR:", result.stderr)
print("Return code:", result.returncode)
