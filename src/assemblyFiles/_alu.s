        lw    0   1   input1
        lw    0   2   input2
        lw    0   5   alu
check   beq   0   5  fand
        lw    0   7   f_or
        beq   7   5  for
        lw    0   7   f_add
        beq   7   5  fadd
fadd    lw    0   3   output
        add   1   2   3
        beq   0   0   end
fand    lw    0   3   output
        nand  1   2   3
        nand  3   3   3
        beq   0   0   end
for     lw    0   3   output
        nand  1   1   1
        nand  2   2   2
        nand  1   2   3
end     halt
input1  .fill   130
input2  .fill   400
alu     .fill   2
f_or    .fill   1
f_add   .fill   2
output  .fill   0