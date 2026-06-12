import os
import glob
import re

files = glob.glob('c:/MEGASOAT-WEB-U3/src/main/resources/static/*.html')
for f in files:
    with open(f, 'r', encoding='utf-8') as file:
        content = file.read()
    
    # Remove the link using regex to handle whitespace
    content = re.sub(r'\s*<a href="/portal\.html">Verificar SOAT</a>\s*', '\n', content)
    
    with open(f, 'w', encoding='utf-8') as file:
        file.write(content)
print("Done")
