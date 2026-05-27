import subprocess

# 使用已知的正确密码哈希（对应密码123456）
correct_hash = '$2a$10$vBccO75ogP3nxoWdyNxlbuG03t5eEobUeGViR2eFgipELeBXDmdZq'

# 更新admin用户密码
cmd_admin = ['mysql', '-u', 'root', '-p123456', '-D', 'suhualin', '-e', f"UPDATE db_account SET password='{correct_hash}' WHERE username='admin'"]
result = subprocess.run(cmd_admin, capture_output=True, text=True)
print("更新admin:", result.stdout, result.stderr)

# 更新馬小風用户密码
cmd_maxiaofeng = ['mysql', '-u', 'root', '-p123456', '-D', 'suhualin', '-e', f"UPDATE db_account SET password='{correct_hash}' WHERE username='馬小風'"]
result = subprocess.run(cmd_maxiaofeng, capture_output=True, text=True)
print("更新馬小風:", result.stdout, result.stderr)

print("密码已更新，现在所有用户密码都是 123456")
