        lw  0   1   n
        lw  0   2   r
        lw  0   6   rAddr
        lw  0   7   stack

combi   beq 1   2   combi2
        beq 2   0   combi2
        lw  0   5   neg1
        add 1   5   1
        lw  0   5   pos1
        sw  7   6   stack
        add 7   5   7
        sw  7   1   stack
        add 7   5   7
        sw  7   2   stack
        add 7   5   7
        lw  0   5   comAdr
        jalr 5  6

        add 3   0   4
        lw  0   5   pos1
        sw  7   4   stack
        lw  0   5   neg1
        add 7   5   7
        lw  7   2   stack
        add 7   5   7
        lw  7   1   stack
        add 2   5   2
        lw  0   5   pos1
        add 7   5   7
        add 7   5   7
        add 7   5   7 
        lw  0   5   comAdr
        jalr 5  6

        lw  0   5   neg1
        add 7   5   7
        lw  7   4   stack
        add 3   4   3
        add 5   6   5
        add 5   6   5
        add 5   6   5
        lw  5   7   stack
        jalr 7  6
        beq 0   0   end
combi2  lw  0   6   pos1
        add 0   6   3
        jalr    7   6
end halt
n       .fill   7
r       .fill   3
rAddr     .fill   end
comAdr     .fill   combi
stack   .fill   0

pos1    .fill    1
neg1    .fill   -1
