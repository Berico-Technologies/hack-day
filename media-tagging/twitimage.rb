#!/usr/bin/env ruby -v

require 'rubygems'
require 'simple-rss'
require 'nokogiri'
require 'exifr'
require 'open-uri'

1..20.times do
    rss = SimpleRSS.parse open('http://twitcaps.com/feed')

    rss.entries.each { | entry |
        link = entry.link

        if link =~ /twitpic.com/
            html = Nokogiri::HTML(open(link))
            html.xpath('//img').each { | img |
                imgSrc = img["src"]

                if imgSrc =~ /(http.*jpg)/
                    begin
                        jpg = EXIFR::JPEG.new(open($1))

                        if jpg.exif?
                            exif = jpg.exif
                            puts exif.gps_latitude
                            puts exif.gps_longitude
                        end
                    rescue
                        # ignore it
                    end
                end
            }
        end
    }
end
