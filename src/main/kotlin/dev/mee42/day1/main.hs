fuel :: Int -> Int
fuel x | x <= 8 = 0
       | otherwise = fuel (x `div` 3 - 2) + x `div` 3 - 2

answer :: IO Int
answer = sum <$> map (fuel . read) <$> lines <$> readFile "res/day1.txt"