#!/usr/bin/awk -f

BEGIN {
  max = 0
  min = 65536
}

{
  col=$1
}

{
  if ($1 + 0 > max + 0)
    max=$1
}

{
  if ($1 + 0 < min + 0)
    min=$1
}

{
  if((col ~  /^-?[0-9]*([.][0-9]+)?$/) && ($0!="")) {
     sum += col;
     a[x++] = col;
     b[col]++
     if (b[col] > hf) {
       hf = b[col]
     }
     sumX2 += col * col
  }
}

END {
  n = asort(a)
  idx = int((x+1)/2)
  avg = sum/x
  print "Count: " x
  print "Sum: " sum
  print "Mean: " avg
  print "Min: " min
  print "Max: " max
  print "Median: " ((idx == (x + 1)/2) ? a[idx] : (a[idx] + a[idx + 1])/2)
  print "Standard Deviation(biased): " sqrt(sumX2/x - avg*avg)
  if (x != 1) {
    print "Standard Deviation(non-biased): " sqrt(sumX2/(x - 1) - 2*avg*(sum/(x - 1)) + x*avg*avg/(x - 1))
  }

  for (i in b) {
    if(b[i] == hf) {
      (k == "") ? (k = i) : (k = k FS i)
    }
    { FS="," }
  }
  print "Mode: " k
}

