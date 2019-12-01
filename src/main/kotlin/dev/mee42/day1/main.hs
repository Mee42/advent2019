fuel :: Int -> Int
fuel x | x <= 8 = 0
       | otherwise = fuel (x `div` 3 - 2) + x `div` 3 - 2

yes :: String -> Int
yes = sum . map (fuel . read) . lines

answer :: IO Int
answer = yes <$> readFile "src/main/resources/day1.txt"