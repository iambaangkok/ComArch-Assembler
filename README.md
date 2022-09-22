
# Work load arrangement
In format of class - person
- Tokenizer - Game
- Assembler - BK
- AssemblyReader - BK

# Specification
## Input specification
- assembly code
- แต่ละคำสั่งอยู่ในบรรทัดของตัวเอง
- `label instruction field0 field1 field2 comments`
    - label: มากสุด 6 ตัวอักษร
    - label: character + number
    - label: ต้องขึ้นต้นด้วยตัวอักษร
    - label: ไม่มีก็ได้
    - field: decimal (เลขฐาน 10) หรือ label
    - R-type: regA, regB, destReg
    - I-type: regA, regB, offsetField
        - symbolic address: คำนวณ offsetField ให้เท่ากับ address ของ label
        - zero-base register ในการอ้างถึง label
        - non zero-base register ในการชี้ไปที่ array
    - J-type: regA, regB
    - O-type: ไม่มี field
    - .fill: field 1 ตัว
        - number → เก็บเลขไว้ใน instruction address นั้น
        - label → เก็บ address ของ label นั้น
    - comments: มี end of line ’\n’
  
## Program specification
- errors ที่ควร detect ได้
    - undefined labels
    - duplicate labels
    - offsetField ที่เกิน 16 bits
    - unrecognized opcode
    - exit(1) → error
    - exit(0) → no error
    - ไม่ต้อง detect error ของ simulator
- offsetField เป็น 2’s compliment: [-32768, 32767]
    - ถ้าเป็น int 32 อย่าลืมเอาแค่ 16 bits negative value

## Output specification
- output as decimal → machine code file
- แต่ละบรรทัดเป็น 1 instruction memory
- * display มี address, hex บอก
- * output มีแค่ decimal

