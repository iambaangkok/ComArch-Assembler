        lw  0   1   n
        lw  0   2   r
        lw  0   5   stack
        lw  0   7   rad
combi   beq 1   2   combi2
        beq 2   0   combi2
        lw  0   6   neg1
        add 1   6   1
        lw  0   6   pos1
        sw  5   7   stack
        add 5   6   5
        sw  5   1   stack
        add 5   6   5
        sw  5   2   stack
        add 5   6   5
        lw  0   6   com
        jalr 6  7
        add 3   0   4
        lw  0   6   pos1
        sw  5   4   stack
        lw  0   6   neg1
        add 5   6   5
        lw  5   2   stack
        add 5   6   5
        lw  5   1   stack
        add 2   6   2
        lw  0   6   pos1
        add 5   6   5
        add 5   6   5
        add 5   6   5 
        lw  0   6   com
        jalr 6  7
        lw  0   6   neg1
        add 5   6   5
        lw  5   4   stack
        add 3   4   3
        add 5   6   5
        add 5   6   5
        add 5   6   5
        lw  5   7   stack
        jalr 7  6
end halt
combi2  lw  0   6   pos1
        add 0   6   3
        jalr    7   6
n       .fill   7
r       .fill   3
pos1    .fill    1
neg1    .fill   -1
com     .fill   combi
rad     .fill   end
stack   .fill   0