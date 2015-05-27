#!/usr/bin/env perl
use strict;
use warnings;

my $ssp = $ENV{SERVER_STARTER_PORT}
    or die "Missing SERVER_STARTER_PORT";
my ($port, $fd) = split /=/, $ssp;

open my $fh, ">&=${fd}"
    or die "Cannot dup ${fd}: $!";
open STDIN, '<&', $fh
    or die "Cannot dup: $!";

exec @ARGV;
die "could not exec(@ARGV): $!";
